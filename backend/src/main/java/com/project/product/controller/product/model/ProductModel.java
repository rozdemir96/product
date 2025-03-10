package com.project.product.controller.product.model;

import com.project.product.constants.ProductType;
import com.project.product.model.BaseModel;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductModel extends BaseModel {
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;

    @Enumerated(EnumType.STRING)
    private ProductType type;  // Ürün tipi
}
