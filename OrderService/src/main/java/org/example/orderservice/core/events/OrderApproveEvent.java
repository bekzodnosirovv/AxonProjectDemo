package org.example.orderservice.core.events;

import lombok.Data;
import lombok.Value;
import org.example.orderservice.core.model.OrderStatus;

@Value
public class OrderApproveEvent {
    private final String orderId;
    private final OrderStatus orderStatus = OrderStatus.APPROVED;
}
