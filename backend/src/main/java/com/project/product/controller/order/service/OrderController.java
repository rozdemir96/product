package com.project.product.controller.order.service;

import com.project.product.controller.order.model.OrderCreateModel;
import com.project.product.controller.order.model.OrderItemModel;
import com.project.product.controller.order.model.OrderModel;
import com.project.product.service.OrderService;
import org.springframework.web.bind.annotation.RequestMapping;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Sipariş Yönetimi", description = "Sipariş CRUD işlemleri yönetilir.")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Yeni sipariş oluşturma - Sadece CUSTOMER erişebilir.
     */
    @PostMapping("/save")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<OrderCreateModel> save(@RequestBody OrderCreateModel orderCreateModel) {
        OrderCreateModel savedOrder = orderService.save(orderCreateModel);
        return ResponseEntity.ok(savedOrder);
    }

    /**
     * Kullanıcının kendi siparişlerini listeleme - Sadece CUSTOMER erişebilir.
     */
    @GetMapping("/list")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<OrderModel>> getList() {
        List<OrderModel> orders = orderService.getOrdersForCurrentUser();
        return ResponseEntity.ok(orders);
    }

    /**
     * Tüm siparişleri listeleme - Sadece ADMIN ve EMPLOYEE erişebilir.
     */
    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<List<OrderModel>> getAll() {
        List<OrderModel> orders = orderService.getAll();
        return ResponseEntity.ok(orders);
    }

    /**
     * Sipariş ID ile getirme - Sadece ADMIN ve EMPLOYEE erişebilir.
     */
    @GetMapping("/get/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<OrderModel> getById(@PathVariable Long id) {
        return orderService.getById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/getOrderItemsByOrderId/{orderId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE', 'CUSTOMER')")
    public ResponseEntity<List<OrderItemModel>> getOrderItemsByOrderId(@PathVariable Long orderId) {
        List<OrderItemModel> orderItems = orderService.getOrderItemsByOrderId(orderId);
        return ResponseEntity.ok(orderItems);
    }

    /**
     * Sipariş durumunu güncelleme - Sadece ADMIN ve EMPLOYEE erişebilir.
     */
    @PutMapping("/update")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<OrderModel> update(@RequestBody OrderModel orderModel) {
        OrderModel updatedOrder = orderService.update(orderModel);
        return ResponseEntity.ok(updatedOrder);
    }

    /**
     * Sipariş silme (iptal etme) - Sadece ADMIN erişebilir.
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
