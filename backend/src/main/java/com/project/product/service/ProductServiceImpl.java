package com.project.product.service;

import com.project.product.controller.product.model.ProductModel;
import com.project.product.entity.Product;
import com.project.product.mapper.ProductMapper;
import com.project.product.repository.ProductRepository;
import com.project.product.security.AuthenticationFacade;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final AuthenticationFacade authenticationFacade;

    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper, AuthenticationFacade authenticationFacade) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.authenticationFacade = authenticationFacade;
    }

    @Override
    public ProductModel save(ProductModel productModel) {
        Product product = productMapper.toEntity(productModel);
        Product savedProduct = productRepository.save(product);
        return productMapper.toModel(savedProduct);
    }

    @Override
    public List<ProductModel> getList() {
        return productMapper.toModelList(productRepository.findAll());
    }

    @Override
    public Optional<ProductModel> getById(Long id) {
        return productRepository.findById(id)
                .map(productMapper::toModel);
    }

    @Override
    public ProductModel update(ProductModel productModel) {
        Product existingProduct = productRepository.findById(productModel.getId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found!"));

        // `ProductMapper` ile entity'yi güncelle
        productMapper.updateProductFromModel(productModel, existingProduct);

        // Güncellenen veriyi kaydet ve Model'e çevirerek döndür
        return productMapper.toModel(productRepository.save(existingProduct));
    }

    @Transactional
    @Override
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new EntityNotFoundException("Product not found!");
        }

        Long currentUserId = authenticationFacade.getCurrentUserId();
        productRepository.softDelete(id, currentUserId);
    }
}
