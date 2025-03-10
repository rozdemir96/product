package com.project.product.repository;

import com.project.product.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE Order o SET o.deletedDate = CURRENT_TIMESTAMP, o.deletedUserId = :userId WHERE o.id = :id")
    void softDelete(@Param("id") Long id, @Param("userId") Long userId);
}
