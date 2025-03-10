package com.project.product.service;

import com.project.product.constants.OrderStatus;
import com.project.product.constants.PaymentStatus;
import com.project.product.constants.Role;
import com.project.product.controller.order.model.OrderCreateModel;
import com.project.product.controller.order.model.OrderItemCreateModel;
import com.project.product.controller.order.model.OrderItemModel;
import com.project.product.controller.order.model.OrderModel;
import com.project.product.entity.Order;
import com.project.product.entity.OrderItem;
import com.project.product.entity.Product;
import com.project.product.entity.User;
import com.project.product.mapper.OrderMapper;
import com.project.product.repository.OrderRepository;
import com.project.product.repository.ProductRepository;
import com.project.product.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.project.product.security.AuthenticationFacade;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.util.*;


@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;
    private final AuthenticationFacade authenticationFacade; // Kullanıcının kimliğini almak için

    @Value("${app.load.test.data:false}")
    private Boolean isTestEnv;

    public OrderServiceImpl(OrderRepository orderRepository, ProductRepository productRepository, UserRepository userRepository, OrderMapper orderMapper, AuthenticationFacade authenticationFacade) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.orderMapper = orderMapper;
        this.authenticationFacade = authenticationFacade;
    }

    /**
     * Yeni sipariş oluşturma - Sadece müşteri (CUSTOMER) erişebilir.
     */
    @Override
    public OrderCreateModel save(OrderCreateModel orderCreateModel) {
        User currentUser;

        // Test ortamındaysa User'ı doğrudan OrderModel'den al
        if (isTestEnv && orderCreateModel.getUserId() != null) {
            currentUser = userRepository.findById(orderCreateModel.getUserId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + orderCreateModel.getUserId()));
        } else {
            currentUser = authenticationFacade.getCurrentUser();
            if (!currentUser.getRole().equals(Role.CUSTOMER)) {
                throw new AccessDeniedException("Only customers can place orders.");
            }
        }

        Order order = new Order();
        order.setUser(currentUser);
        order.setStatus(OrderStatus.PENDING); // Varsayılan sipariş durumu
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setOrderDate(new Date());

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (OrderItemCreateModel itemModel : orderCreateModel.getOrderItems()) {
            Product product = productRepository.findById(itemModel.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + itemModel.getProductId()));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemModel.getQuantity());
            orderItem.setPriceAtPurchase(product.getPrice());

            totalPrice = totalPrice.add(product.getPrice().multiply(BigDecimal.valueOf(itemModel.getQuantity())));

            orderItems.add(orderItem);
        }

        order.setOrderItems(orderItems);
        order.setTotalPrice(totalPrice);

        Order savedOrder = orderRepository.save(order);

        return orderMapper.toCreateModel(savedOrder);
    }

    /**
     * Giriş yapan kullanıcının kendi siparişlerini getirme - Sadece CUSTOMER erişebilir.
     */
    @Override
    public List<OrderModel> getOrdersForCurrentUser() {
        User currentUser = authenticationFacade.getCurrentUser();
        return orderMapper.toModelList(orderRepository.findByUserId(currentUser.getId()));
    }

    /**
     * Tüm siparişleri listeleme - Sadece ADMIN ve EMPLOYEE erişebilir.
     */
    @Override
    public List<OrderModel> getAll() {
        User currentUser = authenticationFacade.getCurrentUser();
        if (!currentUser.getRole().equals(Role.ADMIN) && !currentUser.getRole().equals(Role.EMPLOYEE)) {
            throw new AccessDeniedException("Only admins and employees can view all orders.");
        }
        return orderMapper.toModelList(orderRepository.findAll());
    }

    /**
     * ID ile sipariş getirme - Sadece ADMIN ve EMPLOYEE erişebilir.
     */
    @Override
    public Optional<OrderModel> getById(Long id) {
        User currentUser = authenticationFacade.getCurrentUser();
        if (!currentUser.getRole().equals(Role.ADMIN) && !currentUser.getRole().equals(Role.EMPLOYEE)) {
            throw new AccessDeniedException("Only admins and employees can view orders.");
        }
        return orderRepository.findById(id).map(orderMapper::toModel);
    }

    /**
     * Sipariş durumunu güncelleme - Sadece ADMIN ve EMPLOYEE erişebilir.
     */
    @Override
    public OrderModel update(OrderModel orderModel) {
        User currentUser = authenticationFacade.getCurrentUser();
        if (!currentUser.getRole().equals(Role.ADMIN) && !currentUser.getRole().equals(Role.EMPLOYEE)) {
            throw new AccessDeniedException("Only admins and employees can update orders.");
        }

        Order existingOrder = orderRepository.findById(orderModel.getId())
                .orElseThrow(() -> new EntityNotFoundException("Order not found!"));

        orderMapper.updateOrderFromModel(orderModel, existingOrder);

        return orderMapper.toModel(orderRepository.save(existingOrder));
    }

    /**
     * Siparişi iptal etme (silme) - Sadece ADMIN erişebilir.
     */
    @Override
    public void delete(Long id) {
        User currentUser = authenticationFacade.getCurrentUser();
        if (!currentUser.getRole().equals(Role.ADMIN)) {
            throw new AccessDeniedException("Only admins can delete orders.");
        }
        if (!orderRepository.existsById(id)) {
            throw new EntityNotFoundException("Order not found!");
        }

        Long currentUserId = authenticationFacade.getCurrentUserId();
        orderRepository.softDelete(id, currentUserId);
    }

    @Override
    public List<OrderItemModel> getOrderItemsByOrderId(Long orderId) {
        User currentUser = authenticationFacade.getCurrentUser();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        if (currentUser.getRole() == Role.CUSTOMER && !order.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You can only view your own orders.");
        }

        return orderMapper.toModel(order).getOrderItems();
    }

}
