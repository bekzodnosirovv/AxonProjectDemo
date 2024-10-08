package org.example.orderservice.command.rest;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateOrderRestModel {

    @NotBlank(message = "OrderId is required field")
    private String productId;
    @Min(value = 1, message = "Quantity cannot be lower than 1")
    @Max(value = 10, message = "Quantity cannot be larger than 10")
    private Integer quantity;
    @NotBlank(message = "AddressId is required field")
    private String addressId;
}
