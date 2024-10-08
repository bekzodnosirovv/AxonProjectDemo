package org.example.orderservice.query;

import org.axonframework.queryhandling.QueryHandler;
import org.example.orderservice.core.data.OrderEntity;
import org.example.orderservice.core.data.OrderRepository;
import org.example.orderservice.core.model.OrderSummary;
import org.example.orderservice.query.rest.OrderRestModel;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrderQueryHandler {

    private final OrderRepository orderRepository;

    public OrderQueryHandler(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @QueryHandler
    public List<OrderRestModel> findOrders(FindOrderQuery findOrderQuery) {

        List<OrderEntity> orders = orderRepository.findAll();
        List<OrderRestModel> orderRestModels = new ArrayList<>();
        for (OrderEntity order : orders) {
            OrderRestModel orderRestModel = new OrderRestModel();
            BeanUtils.copyProperties(order, orderRestModel);
            orderRestModels.add(orderRestModel);
        }
        return orderRestModels;
    }

    @QueryHandler
    public OrderSummary findOrder(FindOrderQuery findOrderQuery) {
        OrderEntity orderEntity = orderRepository.findByOrderId(findOrderQuery.getOrderId());
        return new OrderSummary(orderEntity.getOrderId(), orderEntity.getOrderStatus(), "");
    }

}
