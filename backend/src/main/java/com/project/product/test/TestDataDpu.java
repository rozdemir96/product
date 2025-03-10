package com.project.product.test;

import com.project.product.constants.OrderStatus;
import com.project.product.constants.PaymentStatus;
import com.project.product.constants.ProductType;
import com.project.product.constants.Role;
import com.project.product.controller.order.model.OrderCreateModel;
import com.project.product.controller.order.model.OrderItemCreateModel;
import com.project.product.controller.order.model.OrderItemModel;
import com.project.product.controller.order.model.OrderModel;
import com.project.product.controller.product.model.ProductModel;
import com.project.product.controller.user.model.UserModel;
import com.project.product.service.OrderService;
import com.project.product.service.ProductService;
import com.project.product.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Component
@Order(2)
public class TestDataDpu implements ApplicationListener<DpuStarterEvent> {

    private final ApplicationContext appContext;
    private final UserService userService;
    private final ProductService productService;
    private final OrderService orderService;

    private UserModel adminUser;
    private UserModel employeeUser;
    private UserModel janeDoe;
    private UserModel joeDoe;

    private ProductModel laptop;
    private ProductModel tShirt;
    private ProductModel pizza;
    private ProductModel sofa;
    private ProductModel smartphone;

    private OrderCreateModel janeOrder;
    private OrderCreateModel joeOrder;

    @Value("${app.load.test.data:false}")
    private Boolean isTestEnv;

    public TestDataDpu(ApplicationContext appContext, UserService userService, ProductService productService, OrderService orderService) {
        this.appContext = appContext;
        this.userService = userService;
        this.productService = productService;
        this.orderService = orderService;
    }

    @Override
    public void onApplicationEvent(DpuStarterEvent event) {
        try {
            if (this.isTestEnv) {
                this.createTestUsers();
                this.createTestProducts();
                this.createTestOrders();
            }
        } catch (Exception e) {
            e.printStackTrace();
            SpringApplication.exit(this.appContext, () -> 1);
            System.exit(1);
        }
    }

    private void createTestUsers() {

        adminUser = userService.save(new UserModel(
                "Admin", "User", "admin@example.com", "+1234567890", "Admin Address", "admin", "123456", true, Role.ADMIN
        ));

        employeeUser = userService.save(new UserModel(
                "Alice", "Brown", "alice.brown@example.com", "+1222333444", "789 Pine St, Chicago", "alicebrown", "123456", true, Role.EMPLOYEE
        ));

        janeDoe = userService.save(new UserModel(
                "Jane", "Doe", "jane.doe@example.com", "+1122334455", "456 Elm St, Los Angeles", "janedoe", "123456", true, Role.CUSTOMER
        ));

        joeDoe = userService.save(new UserModel(
                "Joe", "Doe", "joe.doe@example.com", "+1987654321", "789 Oak St, San Francisco", "joedoe", "123456", true, Role.CUSTOMER
        ));

    }

    private void createTestProducts() {
        laptop = productService.save(new ProductModel(
                "Laptop", "High performance gaming laptop", new BigDecimal("2500.00"), 10, ProductType.ELECTRONICS
        ));

        tShirt = productService.save(new ProductModel(
                "T-Shirt", "100% Cotton comfortable t-shirt", new BigDecimal("20.99"), 50, ProductType.CLOTHING
        ));

        pizza = productService.save(new ProductModel(
                "Pizza", "Delicious pepperoni pizza", new BigDecimal("15.99"), 30, ProductType.FOOD
        ));

        sofa = productService.save(new ProductModel(
                "Sofa", "Luxury leather sofa", new BigDecimal("899.99"), 5, ProductType.FURNITURE
        ));

        smartphone = productService.save(new ProductModel(
                "Smartphone", "Latest model smartphone with high-end specs", new BigDecimal("1200.00"), 15, ProductType.ELECTRONICS
        ));
    }

    private void createTestOrders() {
        List<OrderItemCreateModel> janeOrderItems = List.of(
                new OrderItemCreateModel(laptop.getId(), 1, laptop.getPrice()),
                new OrderItemCreateModel(tShirt.getId(), 2, tShirt.getPrice())
        );

        janeOrder = new OrderCreateModel(
                janeDoe.getId(),
                janeOrderItems
        );

        List<OrderItemCreateModel> joeOrderItems = List.of(
                new OrderItemCreateModel(pizza.getId(), 1, pizza.getPrice()),
                new OrderItemCreateModel(sofa.getId(), 1, sofa.getPrice()),
                new OrderItemCreateModel(smartphone.getId(), 1, smartphone.getPrice())
        );

        joeOrder = new OrderCreateModel(
                joeDoe.getId(),
                joeOrderItems
        );

        List<OrderCreateModel> orderModelList = List.of(janeOrder, joeOrder);

        for (OrderCreateModel orderCreateModel : orderModelList) {
            orderService.save(orderCreateModel);
        }
    }

}
