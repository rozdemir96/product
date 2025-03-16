package com.project.product.mapper;

import com.project.product.controller.product.model.ProductModel;
import com.project.product.entity.Product;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-03-16T23:20:41+0300",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.12.1.jar, environment: Java 21.0.5 (Oracle Corporation)"
)
@Component
public class ProductMapperImpl implements ProductMapper {

    @Override
    public Product toEntity(ProductModel productModel) {
        if ( productModel == null ) {
            return null;
        }

        Product product = new Product();

        product.setId( productModel.getId() );
        product.setCreatedUserId( productModel.getCreatedUserId() );
        product.setUpdatedUserId( productModel.getUpdatedUserId() );
        product.setDeletedUserId( productModel.getDeletedUserId() );
        product.setCreatedDate( productModel.getCreatedDate() );
        product.setUpdatedDate( productModel.getUpdatedDate() );
        product.setDeletedDate( productModel.getDeletedDate() );
        product.setName( productModel.getName() );
        product.setDescription( productModel.getDescription() );
        product.setPrice( productModel.getPrice() );
        product.setStock( productModel.getStock() );
        product.setType( productModel.getType() );

        return product;
    }

    @Override
    public ProductModel toModel(Product product) {
        if ( product == null ) {
            return null;
        }

        ProductModel productModel = new ProductModel();

        productModel.setId( product.getId() );
        productModel.setCreatedUserId( product.getCreatedUserId() );
        productModel.setUpdatedUserId( product.getUpdatedUserId() );
        productModel.setDeletedUserId( product.getDeletedUserId() );
        productModel.setCreatedDate( product.getCreatedDate() );
        productModel.setUpdatedDate( product.getUpdatedDate() );
        productModel.setDeletedDate( product.getDeletedDate() );
        productModel.setName( product.getName() );
        productModel.setDescription( product.getDescription() );
        productModel.setPrice( product.getPrice() );
        productModel.setStock( product.getStock() );
        productModel.setType( product.getType() );

        return productModel;
    }

    @Override
    public List<Product> toEntityList(List<ProductModel> productModelList) {
        if ( productModelList == null ) {
            return null;
        }

        List<Product> list = new ArrayList<Product>( productModelList.size() );
        for ( ProductModel productModel : productModelList ) {
            list.add( toEntity( productModel ) );
        }

        return list;
    }

    @Override
    public List<ProductModel> toModelList(List<Product> productList) {
        if ( productList == null ) {
            return null;
        }

        List<ProductModel> list = new ArrayList<ProductModel>( productList.size() );
        for ( Product product : productList ) {
            list.add( toModel( product ) );
        }

        return list;
    }

    @Override
    public void updateProductFromModel(ProductModel productModel, Product product) {
        if ( productModel == null ) {
            return;
        }

        product.setId( productModel.getId() );
        product.setCreatedUserId( productModel.getCreatedUserId() );
        product.setUpdatedUserId( productModel.getUpdatedUserId() );
        product.setDeletedUserId( productModel.getDeletedUserId() );
        product.setCreatedDate( productModel.getCreatedDate() );
        product.setUpdatedDate( productModel.getUpdatedDate() );
        product.setDeletedDate( productModel.getDeletedDate() );
        product.setName( productModel.getName() );
        product.setDescription( productModel.getDescription() );
        product.setPrice( productModel.getPrice() );
        product.setStock( productModel.getStock() );
        product.setType( productModel.getType() );
    }
}
