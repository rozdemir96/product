package com.project.product.mapper;

import com.project.product.controller.order.model.OrderCreateModel;
import com.project.product.controller.order.model.OrderModel;
import com.project.product.entity.Order;
import com.project.product.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class}) // OrderItem için ayrı mapper kullanıldı
public interface OrderMapper {

    @Mapping(target = "user", source = "userId", qualifiedByName = "mapUserIdToUser") // userId -> User
    @Mapping(target = "orderItems", source = "orderItems") // OrderItemModel -> OrderItem dönüşümü
    Order toEntity(OrderModel orderModel);

    @Mapping(target = "userId", source = "user.id") // User -> userId dönüşümü
    @Mapping(target = "orderItems", source = "orderItems") // OrderItem -> OrderItemModel dönüşümü
    OrderModel toModel(Order order);

    @Mapping(target = "userId", source = "user.id") // User -> userId dönüşümü
    @Mapping(target = "orderItems", source = "orderItems") // OrderItem -> OrderItemModel dönüşümü
    OrderCreateModel toCreateModel(Order order);

    // **Liste dönüşümleri**
    List<Order> toEntityList(List<OrderModel> orderModelList);
    List<OrderModel> toModelList(List<Order> orderList);

    // **Güncelleme için MapStruct `@MappingTarget` kullanımı**
    @Mapping(target = "id", ignore = true) // ID değiştirilemez
    @Mapping(target = "user", ignore = true) // User değiştirilemez
    void updateOrderFromModel(OrderModel orderModel, @MappingTarget Order order);

    @Named("mapUserIdToUser")
    default User mapUserIdToUser(Long userId) {
        if (userId == null) return null;
        User user = new User();
        user.setId(userId);
        return user;
    }
}
