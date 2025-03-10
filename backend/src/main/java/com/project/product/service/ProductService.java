package com.project.product.service;

import com.project.product.controller.product.model.ProductModel;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    ProductModel save(ProductModel productModel);
    List<ProductModel> getList();
    Optional<ProductModel> getById(Long id);
    ProductModel update(ProductModel productModel);
    void delete(Long id);
}
