package org.example.orderservice.core.events;


import lombok.Value;
import org.example.orderservice.core.model.OrderStatus;

@Value
public class OrderRejectEvent {
    private final String orderId;
    private final String reason;
    private final OrderStatus orderStatus = OrderStatus.REJECTED;
}
