package com.project.product.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderItemServiceImplTest {
    @Test
    void shouldInstantiateService() {
        OrderItemServiceImpl service = new OrderItemServiceImpl();
        assertNotNull(service);
    }
}

