package org.example.core.events;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class ProductReservedEvent implements Serializable {
    private final String productId;
    private final Integer quantity;
    private final String orderId;
    private final String userId;
}
