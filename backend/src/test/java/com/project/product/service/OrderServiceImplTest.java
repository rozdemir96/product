package com.project.product.service;

import com.project.product.constants.Role;
import com.project.product.controller.order.model.OrderCreateModel;
import com.project.product.controller.order.model.OrderItemCreateModel;
import com.project.product.controller.order.model.OrderItemModel;
import com.project.product.controller.order.model.OrderModel;
import com.project.product.entity.Order;
import com.project.product.entity.Product;
import com.project.product.entity.User;
import com.project.product.mapper.OrderMapper;
import com.project.product.repository.OrderRepository;
import com.project.product.repository.ProductRepository;
import com.project.product.repository.UserRepository;
import com.project.product.security.AuthenticationFacade;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {
    @Mock private OrderRepository orderRepository;
    @Mock private ProductRepository productRepository;
    @Mock private UserRepository userRepository;
    @Mock private OrderMapper orderMapper;
    @Mock private AuthenticationFacade authenticationFacade;

    @InjectMocks private OrderServiceImpl orderService;

    private User userWithRole(Role role, Long id) {
        User u = new User();
        u.setId(id);
        u.setRole(role);
        return u;
    }

    private void setIsTestEnv(boolean value) {
        try {
            var f = OrderServiceImpl.class.getDeclaredField("isTestEnv");
            f.setAccessible(true);
            f.set(orderService, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void save_whenCustomer_createsOrder_andMapsBack() {
        // arrange
        setIsTestEnv(false);
        User current = userWithRole(Role.CUSTOMER, 1L);
        when(authenticationFacade.getCurrentUser()).thenReturn(current);

        OrderItemCreateModel item1 = new OrderItemCreateModel(100L, 2, null);
        OrderItemCreateModel item2 = new OrderItemCreateModel(200L, 1, null);
        OrderCreateModel createModel = new OrderCreateModel(null, List.of(item1, item2));

        Product p1 = new Product(); p1.setId(100L); p1.setPrice(new BigDecimal("10.00"));
        Product p2 = new Product(); p2.setId(200L); p2.setPrice(new BigDecimal("5.50"));
        when(productRepository.findById(100L)).thenReturn(Optional.of(p1));
        when(productRepository.findById(200L)).thenReturn(Optional.of(p2));

        Order saved = new Order();
        when(orderRepository.save(any(Order.class))).thenReturn(saved);

        OrderCreateModel mappedBack = new OrderCreateModel();
        when(orderMapper.toCreateModel(saved)).thenReturn(mappedBack);

        // act
        OrderCreateModel result = orderService.save(createModel);

        // assert
        assertSame(mappedBack, result);
        verify(authenticationFacade).getCurrentUser();
        verify(productRepository).findById(100L);
        verify(productRepository).findById(200L);
        verify(orderRepository).save(any(Order.class));
        verify(orderMapper).toCreateModel(saved);
        verifyNoMoreInteractions(orderRepository, productRepository, orderMapper);
    }

    @Test
    void save_inTestEnv_withUserId_shouldUseUserFromRepo() {
        // arrange: simulate test env via reflection by setting isTestEnv=true
        setIsTestEnv(true);

        User user = userWithRole(Role.CUSTOMER, 5L);
        when(userRepository.findById(5L)).thenReturn(Optional.of(user));

        OrderItemCreateModel item = new OrderItemCreateModel(100L, 1, null);
        OrderCreateModel createModel = new OrderCreateModel(5L, List.of(item));

        Product p = new Product(); p.setId(100L); p.setPrice(new BigDecimal("7.00"));
        when(productRepository.findById(100L)).thenReturn(Optional.of(p));

        Order saved = new Order();
        when(orderRepository.save(any(Order.class))).thenReturn(saved);
        when(orderMapper.toCreateModel(saved)).thenReturn(new OrderCreateModel());

        // act
        OrderCreateModel result = orderService.save(createModel);

        // assert
        assertNotNull(result);
        verify(userRepository).findById(5L);
        verify(productRepository).findById(100L);
        verify(orderRepository).save(any(Order.class));
        verify(orderMapper).toCreateModel(saved);
    }

    @Test
    void save_whenNonCustomer_shouldThrowAccessDenied() {
        setIsTestEnv(false);
        User current = userWithRole(Role.ADMIN, 1L);
        when(authenticationFacade.getCurrentUser()).thenReturn(current);

        OrderCreateModel createModel = new OrderCreateModel(null, List.of());
        assertThrows(AccessDeniedException.class, () -> orderService.save(createModel));
    }

    @Test
    void getOrdersForCurrentUser_shouldReturnMappedList() {
        User current = userWithRole(Role.CUSTOMER, 10L);
        when(authenticationFacade.getCurrentUser()).thenReturn(current);
        when(orderRepository.findByUserId(10L)).thenReturn(List.of(new Order()));
        when(orderMapper.toModelList(anyList())).thenReturn(List.of(new OrderModel()));

        List<OrderModel> result = orderService.getOrdersForCurrentUser();
        assertEquals(1, result.size());
        verify(authenticationFacade).getCurrentUser();
        verify(orderRepository).findByUserId(10L);
        verify(orderMapper).toModelList(anyList());
    }

    @Test
    void getAll_whenAdmin_shouldReturnMappedList() {
        User admin = userWithRole(Role.ADMIN, 1L);
        when(authenticationFacade.getCurrentUser()).thenReturn(admin);
        when(orderRepository.findAll()).thenReturn(List.of(new Order()));
        when(orderMapper.toModelList(anyList())).thenReturn(List.of(new OrderModel()));

        List<OrderModel> result = orderService.getAll();
        assertEquals(1, result.size());
        verify(authenticationFacade).getCurrentUser();
        verify(orderRepository).findAll();
        verify(orderMapper).toModelList(anyList());
    }

    @Test
    void getAll_whenCustomer_shouldThrowAccessDenied() {
        User customer = userWithRole(Role.CUSTOMER, 2L);
        when(authenticationFacade.getCurrentUser()).thenReturn(customer);
        assertThrows(AccessDeniedException.class, () -> orderService.getAll());
    }

    @Test
    void getById_whenAdmin_andFound_shouldReturnMappedOptional() {
        User admin = userWithRole(Role.ADMIN, 1L);
        when(authenticationFacade.getCurrentUser()).thenReturn(admin);
        Order order = new Order();
        when(orderRepository.findById(100L)).thenReturn(Optional.of(order));
        OrderModel mapped = new OrderModel();
        when(orderMapper.toModel(order)).thenReturn(mapped);

        Optional<OrderModel> result = orderService.getById(100L);
        assertTrue(result.isPresent());
        assertSame(mapped, result.get());
    }

    @Test
    void getById_whenCustomer_shouldThrowAccessDenied() {
        User customer = userWithRole(Role.CUSTOMER, 3L);
        when(authenticationFacade.getCurrentUser()).thenReturn(customer);
        assertThrows(AccessDeniedException.class, () -> orderService.getById(1L));
    }

    @Test
    void update_whenEmployee_andOrderExists_shouldUpdateAndReturnModel() {
        User employee = userWithRole(Role.EMPLOYEE, 1L);
        when(authenticationFacade.getCurrentUser()).thenReturn(employee);

        OrderModel incoming = new OrderModel(); incoming.setId(55L);
        Order existing = new Order();
        when(orderRepository.findById(55L)).thenReturn(Optional.of(existing));
        Order saved = new Order();
        when(orderRepository.save(existing)).thenReturn(saved);
        OrderModel mapped = new OrderModel();
        when(orderMapper.toModel(saved)).thenReturn(mapped);

        OrderModel result = orderService.update(incoming);
        assertSame(mapped, result);
        verify(orderMapper).updateOrderFromModel(incoming, existing);
    }

    @Test
    void update_whenCustomer_shouldThrowAccessDenied() {
        User customer = userWithRole(Role.CUSTOMER, 1L);
        when(authenticationFacade.getCurrentUser()).thenReturn(customer);
        assertThrows(AccessDeniedException.class, () -> orderService.update(new OrderModel()));
    }

    @Test
    void update_whenNotFound_shouldThrow() {
        User admin = userWithRole(Role.ADMIN, 1L);
        when(authenticationFacade.getCurrentUser()).thenReturn(admin);
        OrderModel incoming = new OrderModel(); incoming.setId(77L);
        when(orderRepository.findById(77L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> orderService.update(incoming));
    }

    @Test
    void delete_whenAdmin_andExists_shouldSoftDeleteWithCurrentUserId() {
        User admin = userWithRole(Role.ADMIN, 1L);
        when(authenticationFacade.getCurrentUser()).thenReturn(admin);
        when(orderRepository.existsById(10L)).thenReturn(true);
        when(authenticationFacade.getCurrentUserId()).thenReturn(1L);

        orderService.delete(10L);

        verify(orderRepository).existsById(10L);
        verify(authenticationFacade).getCurrentUserId();
        verify(orderRepository).softDelete(10L, 1L);
    }

    @Test
    void delete_whenNotAdmin_shouldThrowAccessDenied() {
        User employee = userWithRole(Role.EMPLOYEE, 1L);
        when(authenticationFacade.getCurrentUser()).thenReturn(employee);
        assertThrows(AccessDeniedException.class, () -> orderService.delete(1L));
    }

    @Test
    void delete_whenAdmin_butNotExists_shouldThrowNotFound() {
        User admin = userWithRole(Role.ADMIN, 1L);
        when(authenticationFacade.getCurrentUser()).thenReturn(admin);
        when(orderRepository.existsById(1L)).thenReturn(false);
        assertThrows(EntityNotFoundException.class, () -> orderService.delete(1L));
    }

    @Test
    void getOrderItemsByOrderId_whenCustomerViewingOthersOrder_shouldThrow() {
        User customer = userWithRole(Role.CUSTOMER, 1L);
        when(authenticationFacade.getCurrentUser()).thenReturn(customer);
        Order order = new Order();
        User owner = new User(); owner.setId(2L); order.setUser(owner);
        when(orderRepository.findById(5L)).thenReturn(Optional.of(order));

        assertThrows(AccessDeniedException.class, () -> orderService.getOrderItemsByOrderId(5L));
    }

    @Test
    void getOrderItemsByOrderId_whenAllowed_shouldReturnItemsFromMappedModel() {
        User admin = userWithRole(Role.ADMIN, 1L);
        when(authenticationFacade.getCurrentUser()).thenReturn(admin);
        Order order = new Order(); when(orderRepository.findById(9L)).thenReturn(Optional.of(order));
        OrderModel mapped = new OrderModel();
        mapped.setOrderItems(List.of(new OrderItemModel()));
        when(orderMapper.toModel(order)).thenReturn(mapped);

        List<OrderItemModel> items = orderService.getOrderItemsByOrderId(9L);
        assertEquals(1, items.size());
    }
}
