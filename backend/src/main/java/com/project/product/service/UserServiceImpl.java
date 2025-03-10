package com.project.product.service;

import com.project.product.controller.user.model.UserModel;
import com.project.product.entity.User;
import com.project.product.mapper.UserMapper;
import com.project.product.repository.UserRepository;
import com.project.product.security.AuthenticationFacade;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AuthenticationFacade authenticationFacade;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, AuthenticationFacade authenticationFacade) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.authenticationFacade = authenticationFacade;
    }

    /**
     * Yeni kullanıcı kaydeder.
     * Eğer email veya username sistemde kayıtlıysa hata fırlatır.
     */
    @Override
    public UserModel save(UserModel userModel) {
        if (userRepository.findByEmail(userModel.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists: " + userModel.getEmail());
        }

        if (userRepository.findByUsername(userModel.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists: " + userModel.getUsername());
        }

        User user = userMapper.toEntity(userModel);
        user.setPassword(passwordEncoder.encode(userModel.getPassword()));
        UserModel model = userMapper.toModel(userRepository.save(user));
        model.setPassword(userModel.getPassword());
        return model;
    }

    /**
     * ID’ye göre kullanıcıyı getirir.
     */
    public Optional<UserModel> getById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toModel);
    }

    /**
     * Tüm kullanıcıları getirir.
     */
    @Override
    public List<UserModel> getList() {
        return userMapper.toModelList(userRepository.findAll());
    }

    /**
     * Kullanıcı bilgilerini günceller.
     */
    @Override
    public UserModel update(UserModel userModel) {
        User existingUser = userRepository.findById(userModel.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        userMapper.updateUserFromModel(userModel, existingUser);

        return userMapper.toModel(userRepository.save(existingUser));
    }

    /**
     * Kullanıcıyı ID’ye göre siler.
     */
    @Override
    @Transactional
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found!");
        }

        Long currentUserId = authenticationFacade.getCurrentUserId();
        userRepository.softDelete(id, currentUserId);
    }

    /**
     * E-posta sistemde kayıtlı mı?
     */
    @Override
    public boolean isEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Kullanıcı adı sistemde kayıtlı mı?
     */
    @Override
    public boolean isUsernameExists(String username) {
        return userRepository.existsByUsername(username);
    }
}
