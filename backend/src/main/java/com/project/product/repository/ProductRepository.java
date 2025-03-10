package com.project.product.repository;

import com.project.product.entity.Product;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.deletedDate = CURRENT_TIMESTAMP, p.deletedUserId = :userId WHERE p.id = :id")
    void softDelete(@Param("id") Long id, @Param("userId") Long userId);
}
