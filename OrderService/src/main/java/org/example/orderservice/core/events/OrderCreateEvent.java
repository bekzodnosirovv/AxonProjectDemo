package org.example.orderservice.core.events;

import lombok.Data;
import org.example.orderservice.core.model.OrderStatus;

@Data
public class OrderCreateEvent {

    private String orderId;
    private String userId;
    private String productId;
    private Integer quantity;
    private String addressId;
    private OrderStatus orderStatus;
}
