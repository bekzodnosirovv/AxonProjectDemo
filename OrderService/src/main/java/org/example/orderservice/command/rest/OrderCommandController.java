package org.example.orderservice.command.rest;

import jakarta.validation.Valid;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.SubscriptionQueryResult;
import org.example.orderservice.command.CreateOrderCommand;
import org.example.orderservice.core.data.OrderRepository;
import org.example.orderservice.core.model.OrderStatus;
import org.example.orderservice.core.model.OrderSummary;
import org.example.orderservice.query.FindOrderQuery;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderCommandController {

    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;

    public OrderCommandController(CommandGateway commandGateway, QueryGateway queryGateway) {
        this.commandGateway = commandGateway;
        this.queryGateway = queryGateway;
    }

    @PostMapping
    public OrderSummary orderCreate(@Valid @RequestBody CreateOrderRestModel createOrderRestModel) {

        String userId = "12345";
        String orderId = UUID.randomUUID().toString();

        CreateOrderCommand createOrderCommand = CreateOrderCommand.builder()
                .orderId(orderId)
                .userId(userId)
                .productId(createOrderRestModel.getProductId())
                .quantity(createOrderRestModel.getQuantity())
                .addressId(createOrderRestModel.getAddressId())
                .orderStatus(OrderStatus.CREATED)
                .build();

        SubscriptionQueryResult<OrderSummary, OrderSummary> queryResult =
                queryGateway.subscriptionQuery(new FindOrderQuery(orderId),
                        ResponseTypes.instanceOf(OrderSummary.class),
                        ResponseTypes.instanceOf(OrderSummary.class));

        try {
            commandGateway.send(createOrderCommand);
            return queryResult.updates().blockFirst();
        } finally {
            queryResult.close();
        }
    }
}
