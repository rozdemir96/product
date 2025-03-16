package com.project.product.mapper;

import com.project.product.controller.order.model.OrderItemModel;
import com.project.product.entity.OrderItem;
import com.project.product.entity.Product;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-03-16T23:20:41+0300",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.12.1.jar, environment: Java 21.0.5 (Oracle Corporation)"
)
@Component
public class OrderItemMapperImpl implements OrderItemMapper {

    @Override
    public OrderItem toEntity(OrderItemModel orderItemModel) {
        if ( orderItemModel == null ) {
            return null;
        }

        OrderItem orderItem = new OrderItem();

        orderItem.setProduct( mapProductIdToProduct( orderItemModel.getProductId() ) );
        orderItem.setId( orderItemModel.getId() );
        orderItem.setCreatedUserId( orderItemModel.getCreatedUserId() );
        orderItem.setUpdatedUserId( orderItemModel.getUpdatedUserId() );
        orderItem.setDeletedUserId( orderItemModel.getDeletedUserId() );
        orderItem.setCreatedDate( orderItemModel.getCreatedDate() );
        orderItem.setUpdatedDate( orderItemModel.getUpdatedDate() );
        orderItem.setDeletedDate( orderItemModel.getDeletedDate() );
        orderItem.setQuantity( orderItemModel.getQuantity() );
        orderItem.setPriceAtPurchase( orderItemModel.getPriceAtPurchase() );

        orderItem.setOrder( null );

        return orderItem;
    }

    @Override
    public OrderItemModel toModel(OrderItem orderItem) {
        if ( orderItem == null ) {
            return null;
        }

        OrderItemModel orderItemModel = new OrderItemModel();

        orderItemModel.setProductId( orderItemProductId( orderItem ) );
        orderItemModel.setProductName( orderItemProductName( orderItem ) );
        orderItemModel.setId( orderItem.getId() );
        orderItemModel.setCreatedUserId( orderItem.getCreatedUserId() );
        orderItemModel.setUpdatedUserId( orderItem.getUpdatedUserId() );
        orderItemModel.setDeletedUserId( orderItem.getDeletedUserId() );
        orderItemModel.setCreatedDate( orderItem.getCreatedDate() );
        orderItemModel.setUpdatedDate( orderItem.getUpdatedDate() );
        orderItemModel.setDeletedDate( orderItem.getDeletedDate() );
        orderItemModel.setQuantity( orderItem.getQuantity() );
        orderItemModel.setPriceAtPurchase( orderItem.getPriceAtPurchase() );

        return orderItemModel;
    }

    @Override
    public List<OrderItem> toEntityList(List<OrderItemModel> orderItemModelList) {
        if ( orderItemModelList == null ) {
            return null;
        }

        List<OrderItem> list = new ArrayList<OrderItem>( orderItemModelList.size() );
        for ( OrderItemModel orderItemModel : orderItemModelList ) {
            list.add( toEntity( orderItemModel ) );
        }

        return list;
    }

    @Override
    public List<OrderItemModel> toModelList(List<OrderItem> orderItemList) {
        if ( orderItemList == null ) {
            return null;
        }

        List<OrderItemModel> list = new ArrayList<OrderItemModel>( orderItemList.size() );
        for ( OrderItem orderItem : orderItemList ) {
            list.add( toModel( orderItem ) );
        }

        return list;
    }

    private Long orderItemProductId(OrderItem orderItem) {
        Product product = orderItem.getProduct();
        if ( product == null ) {
            return null;
        }
        return product.getId();
    }

    private String orderItemProductName(OrderItem orderItem) {
        Product product = orderItem.getProduct();
        if ( product == null ) {
            return null;
        }
        return product.getName();
    }
}
