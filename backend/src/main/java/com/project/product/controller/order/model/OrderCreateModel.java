package com.project.product.controller.order.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateModel {
    private Long userId;
    private List<OrderItemCreateModel> orderItems; // Sadece sipariş kalemleri
}
