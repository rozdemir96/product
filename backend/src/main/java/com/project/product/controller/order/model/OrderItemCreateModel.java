package com.project.product.controller.order.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemCreateModel {
    private Long productId; // Sipariş edilen ürün ID'si
    private Integer quantity;   // Sipariş edilen ürün adeti
    private BigDecimal priceAtPurchase; // Sipariş anındaki ürün fiyatı
}
