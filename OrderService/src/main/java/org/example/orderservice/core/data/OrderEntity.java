package org.example.orderservice.core.data;

import jakarta.persistence.*;
import lombok.Data;
import org.example.orderservice.core.model.OrderStatus;

@Data
@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    private String orderId;
    private String userId;
    private String productId;
    private Integer quantity;
    private String addressId;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
}
