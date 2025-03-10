package com.project.product.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.project.product.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.util.List;


/**
 * Kullanıcının siparişinde hangi üründen kaç adet satın aldığı bilgisini tutar.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order_items")
@SQLDelete(sql = "UPDATE order_items SET deleted_date = CURRENT_TIMESTAMP, deleted_user_id = ? WHERE id = ?")
@Where(clause = "deleted_date IS NULL")
public class OrderItem extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, updatable = false) // Order değiştirilemez!
    @JsonManagedReference
    private Order order;  // Sipariş bilgisi

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;  // Sipariş edilen ürün

    private Integer quantity;

    private BigDecimal priceAtPurchase;  // Sipariş anındaki ürün fiyatı
}