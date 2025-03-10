package com.project.product.mapper;

import com.project.product.controller.order.model.OrderCreateModel;
import com.project.product.controller.order.model.OrderItemCreateModel;
import com.project.product.controller.order.model.OrderModel;
import com.project.product.entity.Order;
import com.project.product.entity.OrderItem;
import com.project.product.entity.User;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-03-10T00:23:04+0300",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.12.1.jar, environment: Java 21.0.5 (Oracle Corporation)"
)
@Component
public class OrderMapperImpl implements OrderMapper {

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Override
    public Order toEntity(OrderModel orderModel) {
        if ( orderModel == null ) {
            return null;
        }

        Order order = new Order();

        order.setUser( mapUserIdToUser( orderModel.getUserId() ) );
        order.setOrderItems( orderItemMapper.toEntityList( orderModel.getOrderItems() ) );
        order.setId( orderModel.getId() );
        order.setCreatedUserId( orderModel.getCreatedUserId() );
        order.setUpdatedUserId( orderModel.getUpdatedUserId() );
        order.setDeletedUserId( orderModel.getDeletedUserId() );
        order.setCreatedDate( orderModel.getCreatedDate() );
        order.setUpdatedDate( orderModel.getUpdatedDate() );
        order.setDeletedDate( orderModel.getDeletedDate() );
        order.setOrderDate( orderModel.getOrderDate() );
        order.setStatus( orderModel.getStatus() );
        order.setPaymentStatus( orderModel.getPaymentStatus() );
        order.setTotalPrice( orderModel.getTotalPrice() );

        return order;
    }

    @Override
    public OrderModel toModel(Order order) {
        if ( order == null ) {
            return null;
        }

        OrderModel orderModel = new OrderModel();

        orderModel.setUserId( orderUserId( order ) );
        orderModel.setOrderItems( orderItemMapper.toModelList( order.getOrderItems() ) );
        orderModel.setId( order.getId() );
        orderModel.setCreatedUserId( order.getCreatedUserId() );
        orderModel.setUpdatedUserId( order.getUpdatedUserId() );
        orderModel.setDeletedUserId( order.getDeletedUserId() );
        orderModel.setCreatedDate( order.getCreatedDate() );
        orderModel.setUpdatedDate( order.getUpdatedDate() );
        orderModel.setDeletedDate( order.getDeletedDate() );
        orderModel.setOrderDate( order.getOrderDate() );
        orderModel.setStatus( order.getStatus() );
        orderModel.setPaymentStatus( order.getPaymentStatus() );
        orderModel.setTotalPrice( order.getTotalPrice() );

        return orderModel;
    }

    @Override
    public OrderCreateModel toCreateModel(Order order) {
        if ( order == null ) {
            return null;
        }

        OrderCreateModel orderCreateModel = new OrderCreateModel();

        orderCreateModel.setUserId( orderUserId( order ) );
        orderCreateModel.setOrderItems( orderItemListToOrderItemCreateModelList( order.getOrderItems() ) );

        return orderCreateModel;
    }

    @Override
    public List<Order> toEntityList(List<OrderModel> orderModelList) {
        if ( orderModelList == null ) {
            return null;
        }

        List<Order> list = new ArrayList<Order>( orderModelList.size() );
        for ( OrderModel orderModel : orderModelList ) {
            list.add( toEntity( orderModel ) );
        }

        return list;
    }

    @Override
    public List<OrderModel> toModelList(List<Order> orderList) {
        if ( orderList == null ) {
            return null;
        }

        List<OrderModel> list = new ArrayList<OrderModel>( orderList.size() );
        for ( Order order : orderList ) {
            list.add( toModel( order ) );
        }

        return list;
    }

    @Override
    public void updateOrderFromModel(OrderModel orderModel, Order order) {
        if ( orderModel == null ) {
            return;
        }

        order.setCreatedUserId( orderModel.getCreatedUserId() );
        order.setUpdatedUserId( orderModel.getUpdatedUserId() );
        order.setDeletedUserId( orderModel.getDeletedUserId() );
        order.setCreatedDate( orderModel.getCreatedDate() );
        order.setUpdatedDate( orderModel.getUpdatedDate() );
        order.setDeletedDate( orderModel.getDeletedDate() );
        order.setOrderDate( orderModel.getOrderDate() );
        order.setStatus( orderModel.getStatus() );
        order.setPaymentStatus( orderModel.getPaymentStatus() );
        order.setTotalPrice( orderModel.getTotalPrice() );
        if ( order.getOrderItems() != null ) {
            List<OrderItem> list = orderItemMapper.toEntityList( orderModel.getOrderItems() );
            if ( list != null ) {
                order.getOrderItems().clear();
                order.getOrderItems().addAll( list );
            }
            else {
                order.setOrderItems( null );
            }
        }
        else {
            List<OrderItem> list = orderItemMapper.toEntityList( orderModel.getOrderItems() );
            if ( list != null ) {
                order.setOrderItems( list );
            }
        }
    }

    private Long orderUserId(Order order) {
        User user = order.getUser();
        if ( user == null ) {
            return null;
        }
        return user.getId();
    }

    protected OrderItemCreateModel orderItemToOrderItemCreateModel(OrderItem orderItem) {
        if ( orderItem == null ) {
            return null;
        }

        OrderItemCreateModel orderItemCreateModel = new OrderItemCreateModel();

        orderItemCreateModel.setQuantity( orderItem.getQuantity() );
        orderItemCreateModel.setPriceAtPurchase( orderItem.getPriceAtPurchase() );

        return orderItemCreateModel;
    }

    protected List<OrderItemCreateModel> orderItemListToOrderItemCreateModelList(List<OrderItem> list) {
        if ( list == null ) {
            return null;
        }

        List<OrderItemCreateModel> list1 = new ArrayList<OrderItemCreateModel>( list.size() );
        for ( OrderItem orderItem : list ) {
            list1.add( orderItemToOrderItemCreateModel( orderItem ) );
        }

        return list1;
    }
}
