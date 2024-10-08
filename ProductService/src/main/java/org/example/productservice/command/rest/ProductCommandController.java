package org.example.productservice.command.rest;

import jakarta.validation.Valid;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.example.productservice.command.CreateProductCommand;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/product")
public class ProductCommandController {

    private final CommandGateway commandGateway;

    public ProductCommandController(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @PostMapping
    public String createProduct(@Valid @RequestBody CreateProductRestModel createProductRestModel) {

        CreateProductCommand createProductCommand = CreateProductCommand.builder()
                .title(createProductRestModel.getTitle())
                .price(createProductRestModel.getPrice())
                .quantity(createProductRestModel.getQuantity())
                .productId(UUID.randomUUID().toString())
                .build();


        String returnvalue = commandGateway.sendAndWait(createProductCommand);

        return returnvalue;
    }
}
