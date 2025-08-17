package com.project.product.service;

import com.project.product.controller.product.model.ProductModel;
import com.project.product.entity.Product;
import com.project.product.mapper.ProductMapper;
import com.project.product.repository.ProductRepository;
import com.project.product.security.AuthenticationFacade;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductMapper productMapper;
    @Mock
    private AuthenticationFacade authenticationFacade;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void save_shouldMapPersistAndReturnModel() {
        // arrange
        ProductModel inputModel = new ProductModel();
        Product productEntity = new Product();
        Product savedProduct = new Product();
        ProductModel outputModel = new ProductModel();

        when(productMapper.toEntity(inputModel)).thenReturn(productEntity);
        when(productRepository.save(productEntity)).thenReturn(savedProduct);
        when(productMapper.toModel(savedProduct)).thenReturn(outputModel);

        // act
        ProductModel result = productService.save(inputModel);

        // assert
        assertSame(outputModel, result);
        verify(productMapper).toEntity(inputModel);
        verify(productRepository).save(productEntity);
        verify(productMapper).toModel(savedProduct);
        verifyNoMoreInteractions(productRepository, productMapper);
        verifyNoInteractions(authenticationFacade);
    }

    @Test
    void getList_shouldReturnMappedList() {
        // arrange
        List<Product> productEntities = List.of(new Product(), new Product());
        List<ProductModel> productModels = List.of(new ProductModel(), new ProductModel());
        when(productRepository.findAll()).thenReturn(productEntities);
        when(productMapper.toModelList(productEntities)).thenReturn(productModels);

        // act
        List<ProductModel> result = productService.getList();

        // assert
        assertEquals(productModels, result);
        verify(productRepository).findAll();
        verify(productMapper).toModelList(productEntities);
        verifyNoMoreInteractions(productRepository, productMapper);
        verifyNoInteractions(authenticationFacade);
    }

    @Test
    void getById_whenFound_shouldReturnMappedOptional() {
        // arrange
        Long id = 1L;
        Product productEntity = new Product();
        ProductModel productModel = new ProductModel();
        when(productRepository.findById(id)).thenReturn(Optional.of(productEntity));
        when(productMapper.toModel(productEntity)).thenReturn(productModel);

        // act
        Optional<ProductModel> result = productService.getById(id);

        // assert
        assertTrue(result.isPresent());
        assertSame(productModel, result.get());
        verify(productRepository).findById(id);
        verify(productMapper).toModel(productEntity);
        verifyNoMoreInteractions(productRepository, productMapper);
        verifyNoInteractions(authenticationFacade);
    }

    @Test
    void getById_whenNotFound_shouldReturnEmptyOptional() {
        // arrange
        Long id = 2L;
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        // act
        Optional<ProductModel> result = productService.getById(id);

        // assert
        assertTrue(result.isEmpty());
        verify(productRepository).findById(id);
        verifyNoMoreInteractions(productRepository);
        verifyNoInteractions(productMapper, authenticationFacade);
    }

    @Test
    void save_whenMapperThrows_shouldPropagateException() {
        // arrange
        ProductModel inputModel = new ProductModel();
        when(productMapper.toEntity(inputModel)).thenThrow(new RuntimeException("Mapping error"));

        // act & assert
        RuntimeException ex = assertThrows(RuntimeException.class, () -> productService.save(inputModel));
        assertEquals("Mapping error", ex.getMessage());
        verify(productMapper).toEntity(inputModel);
        verifyNoInteractions(productRepository);
        verifyNoInteractions(authenticationFacade);
    }

    @Test
    void save_whenRepositoryThrows_shouldPropagateException() {
        // arrange
        ProductModel inputModel = new ProductModel();
        Product productEntity = new Product();
        when(productMapper.toEntity(inputModel)).thenReturn(productEntity);
        when(productRepository.save(productEntity)).thenThrow(new RuntimeException("DB error"));

        // act & assert
        RuntimeException ex = assertThrows(RuntimeException.class, () -> productService.save(inputModel));
        assertEquals("DB error", ex.getMessage());
        verify(productMapper).toEntity(inputModel);
        verify(productRepository).save(productEntity);
        verifyNoMoreInteractions(productRepository, productMapper);
        verifyNoInteractions(authenticationFacade);
    }

    @Test
    void update_whenEntityExists_shouldMapUpdateAndPersist() {
        // arrange
        Long id = 20L;
        ProductModel incoming = new ProductModel();
        incoming.setId(id);

        Product existing = new Product();
        when(productRepository.findById(id)).thenReturn(Optional.of(existing));

        Product saved = new Product();
        when(productRepository.save(existing)).thenReturn(saved);
        ProductModel mappedBack = new ProductModel();
        when(productMapper.toModel(saved)).thenReturn(mappedBack);

        // act
        ProductModel result = productService.update(incoming);

        // assert
        assertSame(mappedBack, result);
        verify(productRepository).findById(id);
        verify(productMapper).updateProductFromModel(incoming, existing);
        verify(productRepository).save(existing);
        verify(productMapper).toModel(saved);
        verifyNoMoreInteractions(productRepository, productMapper);
        verifyNoInteractions(authenticationFacade);
    }

    @Test
    void update_whenEntityMissing_shouldThrowEntityNotFound() {
        // arrange
        Long id = 21L;
        ProductModel incoming = new ProductModel();
        incoming.setId(id);
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        // act & assert
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> productService.update(incoming));
        assertEquals("Product not found!", ex.getMessage());
        verify(productRepository).findById(id);
        verifyNoMoreInteractions(productRepository);
        verifyNoInteractions(productMapper, authenticationFacade);
    }

    @Test
    void delete_whenExists_shouldSoftDeleteWithCurrentUserId() {
        // arrange
        Long id = 30L;
        Long currentUserId = 999L;
        when(productRepository.existsById(id)).thenReturn(true);
        when(authenticationFacade.getCurrentUserId()).thenReturn(currentUserId);

        // act
        productService.delete(id);

        // assert
        verify(productRepository).existsById(id);
        verify(authenticationFacade).getCurrentUserId();
        verify(productRepository).softDelete(id, currentUserId);
        verifyNoMoreInteractions(productRepository, authenticationFacade);
        verifyNoInteractions(productMapper);
    }

    @Test
    void delete_whenExists_andCurrentUserIdNull_shouldSoftDeleteWithNullUser() {
        // arrange
        Long id = 31L;
        when(productRepository.existsById(id)).thenReturn(true);
        when(authenticationFacade.getCurrentUserId()).thenReturn(null);

        // act
        productService.delete(id);

        // assert
        verify(productRepository).existsById(id);
        verify(authenticationFacade).getCurrentUserId();
        verify(productRepository).softDelete(id, null);
        verifyNoMoreInteractions(productRepository, authenticationFacade);
        verifyNoInteractions(productMapper);
    }

    @Test
    void delete_whenNotExists_shouldThrowEntityNotFound() {
        // arrange
        Long id = 32L;
        when(productRepository.existsById(id)).thenReturn(false);

        // act & assert
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> productService.delete(id));
        assertEquals("Product not found!", ex.getMessage());
        verify(productRepository).existsById(id);
        verifyNoMoreInteractions(productRepository);
        verifyNoInteractions(authenticationFacade, productMapper);
    }
}
