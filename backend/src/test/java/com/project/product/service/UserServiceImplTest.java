package com.project.product.service;

import com.project.product.controller.user.model.UserModel;
import com.project.product.entity.User;
import com.project.product.mapper.UserMapper;
import com.project.product.repository.UserRepository;
import com.project.product.security.AuthenticationFacade;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock private UserRepository userRepository;
    @Mock private UserMapper userMapper;
    @Mock private AuthenticationFacade authenticationFacade;

    @InjectMocks private UserServiceImpl userService;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Captor ArgumentCaptor<User> userCaptor;
    @Captor ArgumentCaptor<UserModel> modelCaptor;

    @Test
    void save_shouldEncodePassword_andReturnModelWithOriginalPassword() {
        UserModel input = new UserModel();
        input.setEmail("e@x.com");
        input.setUsername("u");
        input.setPassword("secret");

        when(userRepository.findByEmail("e@x.com")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("u")).thenReturn(Optional.empty());

        User toSave = new User();
        when(userMapper.toEntity(input)).thenReturn(toSave);

        User saved = new User();
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User arg = inv.getArgument(0);
            // propagate the encoded password into returned entity
            saved.setPassword(arg.getPassword());
            return saved;
        });

        UserModel mapped = new UserModel();
        when(userMapper.toModel(saved)).thenReturn(mapped);

        UserModel result = userService.save(input);

        assertSame(mapped, result);
        assertEquals("secret", result.getPassword()); // service restores original into model

        verify(userRepository).findByEmail("e@x.com");
        verify(userRepository).findByUsername("u");
        verify(userMapper).toEntity(input);
        verify(userRepository).save(userCaptor.capture());
        verify(userMapper).toModel(saved);

        String hashed = userCaptor.getValue().getPassword();
        assertNotNull(hashed);
        assertTrue(encoder.matches("secret", hashed));
    }

    @Test
    void save_whenEmailExists_shouldThrow() {
        UserModel input = new UserModel();
        input.setEmail("e@x.com");
        when(userRepository.findByEmail("e@x.com")).thenReturn(Optional.of(new User()));
        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.save(input));
        assertTrue(ex.getMessage().startsWith("Email already exists"));
        verify(userRepository).findByEmail("e@x.com");
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(userMapper);
    }

    @Test
    void save_whenUsernameExists_shouldThrow() {
        UserModel input = new UserModel();
        input.setEmail("e@x.com");
        input.setUsername("u");
        when(userRepository.findByEmail("e@x.com")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("u")).thenReturn(Optional.of(new User()));
        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.save(input));
        assertTrue(ex.getMessage().startsWith("Username already exists"));
    }

    @Test
    void getById_found_shouldReturnMapped() {
        User entity = new User();
        when(userRepository.findById(10L)).thenReturn(Optional.of(entity));
        UserModel mapped = new UserModel();
        when(userMapper.toModel(entity)).thenReturn(mapped);

        Optional<UserModel> result = userService.getById(10L);
        assertTrue(result.isPresent());
        assertSame(mapped, result.get());
    }

    @Test
    void getById_notFound_shouldReturnEmpty() {
        when(userRepository.findById(11L)).thenReturn(Optional.empty());
        assertTrue(userService.getById(11L).isEmpty());
    }

    @Test
    void getList_shouldReturnMappedList() {
        when(userRepository.findAll()).thenReturn(List.of(new User()));
        when(userMapper.toModelList(anyList())).thenReturn(List.of(new UserModel()));
        List<UserModel> result = userService.getList();
        assertEquals(1, result.size());
    }

    @Test
    void update_whenFound_andPasswordProvided_shouldEncodeAndSave() {
        UserModel incoming = new UserModel();
        incoming.setId(5L);
        incoming.setPassword("newpass");

        User existing = new User();
        existing.setPassword(encoder.encode("old"));
        when(userRepository.findById(5L)).thenReturn(Optional.of(existing));

        User saved = new User();
        when(userRepository.save(existing)).thenReturn(saved);
        when(userMapper.toModel(saved)).thenReturn(new UserModel());

        UserModel result = userService.update(incoming);
        assertNotNull(result);

        // capture the model passed into mapper update to assert password handling
        verify(userMapper).updateUserFromModel(modelCaptor.capture(), eq(existing));
        UserModel passed = modelCaptor.getValue();
        assertNotNull(passed.getPassword());
        assertTrue(encoder.matches("newpass", passed.getPassword()));
    }

    @Test
    void update_whenFound_andPasswordBlank_shouldKeepExistingHashed() {
        UserModel incoming = new UserModel();
        incoming.setId(6L);
        incoming.setPassword("   ");

        String existingHash = encoder.encode("old");
        User existing = new User();
        existing.setPassword(existingHash);
        when(userRepository.findById(6L)).thenReturn(Optional.of(existing));

        when(userRepository.save(existing)).thenReturn(existing);
        when(userMapper.toModel(existing)).thenReturn(new UserModel());

        userService.update(incoming);

        verify(userMapper).updateUserFromModel(modelCaptor.capture(), eq(existing));
        assertEquals(existingHash, modelCaptor.getValue().getPassword());
    }

    @Test
    void update_whenNotFound_shouldThrow() {
        UserModel incoming = new UserModel(); incoming.setId(7L);
        when(userRepository.findById(7L)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.update(incoming));
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void delete_whenExists_shouldSoftDeleteWithCurrentUserId() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(authenticationFacade.getCurrentUserId()).thenReturn(99L);

        userService.delete(1L);

        verify(userRepository).existsById(1L);
        verify(authenticationFacade).getCurrentUserId();
        verify(userRepository).softDelete(1L, 99L);
    }

    @Test
    void delete_whenExists_andCurrentUserIdNull_shouldSoftDeleteWithNull() {
        when(userRepository.existsById(2L)).thenReturn(true);
        when(authenticationFacade.getCurrentUserId()).thenReturn(null);

        userService.delete(2L);
        verify(userRepository).softDelete(2L, null);
    }

    @Test
    void delete_whenNotExists_shouldThrow() {
        when(userRepository.existsById(3L)).thenReturn(false);
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> userService.delete(3L));
        assertEquals("User not found!", ex.getMessage());
    }

    @Test
    void isEmailExists_delegatesToRepo() {
        when(userRepository.existsByEmail("a@b.com")).thenReturn(true);
        assertTrue(userService.isEmailExists("a@b.com"));
    }

    @Test
    void isUsernameExists_delegatesToRepo() {
        when(userRepository.existsByUsername("u")).thenReturn(false);
        assertFalse(userService.isUsernameExists("u"));
    }
}

