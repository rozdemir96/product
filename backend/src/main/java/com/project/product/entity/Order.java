package com.project.product.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.project.product.constants.OrderStatus;
import com.project.product.constants.PaymentStatus;
import com.project.product.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
@SQLDelete(sql = "UPDATE orders SET deleted_date = CURRENT_TIMESTAMP, deleted_user_id = ? WHERE id = ?")
@Where(clause = "deleted_date IS NULL")
public class Order extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  // Siparişi veren kullanıcı

    @Temporal(TemporalType.TIMESTAMP)
    private Date orderDate = new Date();

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;  // Ödeme durumu

    private BigDecimal totalPrice;  // Siparişin toplam tutarı

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<OrderItem> orderItems;// Siparişe bağlı sipariş kalemleri
}