package org.example.productservice.command.rest;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateProductRestModel {

    @NotBlank(message = "Product title is required field")
    private String title;
    @Min(value = 1, message = "Price cannot be lower than 1")
    private BigDecimal price;
    @Min(value = 1, message = "Quantity cannot be lower than 1")
    @Max(value = 10, message = "Quantity cannot be larger than 10")
    private Integer quantity;
}
