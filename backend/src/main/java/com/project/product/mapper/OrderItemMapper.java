package com.project.product.mapper;

import com.project.product.controller.order.model.OrderItemModel;
import com.project.product.entity.OrderItem;
import com.project.product.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(target = "order", expression = "java(null)") // Order üst modelden setleniyor
    @Mapping(target = "product", source = "productId", qualifiedByName = "mapProductIdToProduct") // productId -> Product
    OrderItem toEntity(OrderItemModel orderItemModel);

    @Mapping(target = "productId", source = "product.id") // Product -> productId
    @Mapping(target = "productName", source = "product.name")
    OrderItemModel toModel(OrderItem orderItem);

    // **Liste dönüşümleri**
    List<OrderItem> toEntityList(List<OrderItemModel> orderItemModelList);
    List<OrderItemModel> toModelList(List<OrderItem> orderItemList);

    @Named("mapProductIdToProduct")
    default Product mapProductIdToProduct(Long productId) {
        if (productId == null) return null;
        Product product = new Product();
        product.setId(productId);
        return product;
    }
}
