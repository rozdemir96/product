package com.project.product.mapper;

import com.project.product.controller.product.model.ProductModel;
import com.project.product.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product toEntity(ProductModel productModel);

    ProductModel toModel(Product product);

    List<Product> toEntityList(List<ProductModel> productModelList);
    List<ProductModel> toModelList(List<Product> productList);

    // Mevcut entity'yi güncellemek için
    void updateProductFromModel(ProductModel productModel, @MappingTarget Product product);
}
