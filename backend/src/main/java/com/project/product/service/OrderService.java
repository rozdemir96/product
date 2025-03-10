package com.project.product.service;

import com.project.product.controller.order.model.OrderCreateModel;
import com.project.product.controller.order.model.OrderItemModel;
import com.project.product.controller.order.model.OrderModel;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    OrderCreateModel save(OrderCreateModel orderCreateModel);
    List<OrderModel> getOrdersForCurrentUser();
    List<OrderModel> getAll();
    Optional<OrderModel> getById(Long id);
    OrderModel update(OrderModel orderModel);
    void delete(Long id);

    List<OrderItemModel> getOrderItemsByOrderId(Long orderId);
}
