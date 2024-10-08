package org.example.orderservice.command;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.example.orderservice.command.commands.ApproveOrderCommand;
import org.example.orderservice.command.commands.RejectOrderCommand;
import org.example.orderservice.core.events.OrderApproveEvent;
import org.example.orderservice.core.events.OrderCreateEvent;
import org.example.orderservice.core.events.OrderRejectEvent;
import org.example.orderservice.core.model.OrderStatus;
import org.springframework.beans.BeanUtils;

@Aggregate(snapshotTriggerDefinition = "orderSnapshotTriggerDefinition")
public class OrderAggregate {

    @AggregateIdentifier
    private String orderId;
    private String productId;
    private String userId;
    private int quantity;
    private String addressId;
    private OrderStatus orderStatus;


    public OrderAggregate() {
    }

    @CommandHandler
    public OrderAggregate(CreateOrderCommand command) throws Exception {

        OrderCreateEvent event = new OrderCreateEvent();
        BeanUtils.copyProperties(command, event);
        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(OrderCreateEvent event) {
        this.orderId = event.getOrderId();
        this.productId = event.getProductId();
        this.userId = event.getUserId();
        this.quantity = event.getQuantity();
        this.addressId = event.getAddressId();
        this.orderStatus = event.getOrderStatus();
    }


    @CommandHandler
    public void handle(ApproveOrderCommand approveOrderCommand) {
        // Create and publish orderApprovedEvent

        OrderApproveEvent orderApproveEvent = new OrderApproveEvent(approveOrderCommand.getOrderId());
        AggregateLifecycle.apply(orderApproveEvent);
    }

    @EventSourcingHandler
    public void on(OrderApproveEvent event) {
        this.orderStatus = event.getOrderStatus();
    }

    @CommandHandler
    public void handle(RejectOrderCommand command) {

        OrderRejectEvent orderRejectEvent = new OrderRejectEvent(command.getOrderId(), command.getReason());
        AggregateLifecycle.apply(orderRejectEvent);
    }

    @EventSourcingHandler
    public void on(OrderRejectEvent event) {
        this.orderStatus = event.getOrderStatus();
    }


}
