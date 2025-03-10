package com.project.product.controller.order.model;

import com.project.product.constants.OrderStatus;
import com.project.product.constants.PaymentStatus;
import com.project.product.model.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderModel extends BaseModel {
    private Long userId; // Siparişi veren kullanıcı ID'si

    private Date orderDate = new Date();

    private OrderStatus status;

    private PaymentStatus paymentStatus;  // Ödeme durumu

    private BigDecimal totalPrice;  // Siparişin toplam tutarı

    private List<OrderItemModel> orderItems; // Siparişe bağlı sipariş kalemleri
}
