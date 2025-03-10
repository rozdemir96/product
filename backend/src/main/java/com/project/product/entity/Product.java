package com.project.product.entity;

import com.project.product.constants.ProductType;
import com.project.product.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products")
@SQLDelete(sql = "UPDATE products SET deleted_date = CURRENT_TIMESTAMP, deleted_user_id = ? WHERE id = ?")
@Where(clause = "deleted_date IS NULL")
public class Product extends BaseEntity {
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;

    @Enumerated(EnumType.STRING)
    private ProductType type;  // Ürün tipi
}