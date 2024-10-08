package org.example.orderservice.command;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.example.orderservice.core.data.OrderEntity;
import org.example.orderservice.core.data.OrderRepository;
import org.example.orderservice.core.events.OrderApproveEvent;
import org.example.orderservice.core.events.OrderCreateEvent;
import org.example.orderservice.core.events.OrderRejectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Component
@ProcessingGroup("order-group")
public class OrderEventHandler {

    private final Logger log = LoggerFactory.getLogger(OrderEventHandler.class);

    private final OrderRepository orderRepository;

    public OrderEventHandler(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @ExceptionHandler(Exception.class)
    public void handle(Exception exception) throws Exception {
        throw exception;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public void handle(IllegalArgumentException exception) {
        // Log error message
    }

    @EventHandler
    public void on(OrderCreateEvent event) throws Exception {
        OrderEntity orderEntity = new OrderEntity();
        BeanUtils.copyProperties(event, orderEntity);
        orderRepository.save(orderEntity);
    }

    @EventHandler
    public void on(OrderApproveEvent event) throws Exception {
        OrderEntity entity = orderRepository.findByOrderId(event.getOrderId());

        if (entity == null) {
            log.warn("Order does not exist");
            return;
        }

        entity.setOrderStatus(event.getOrderStatus());
        orderRepository.save(entity);

    }

    @EventHandler
    public void on(OrderRejectEvent event) throws Exception {
        log.info(event.toString());

        OrderEntity entity = orderRepository.findByOrderId(event.getOrderId());
        if (entity == null) {
            log.warn("Order does not exist");
            return;
        }
        entity.setOrderStatus(event.getOrderStatus());
        orderRepository.save(entity);

    }

}
