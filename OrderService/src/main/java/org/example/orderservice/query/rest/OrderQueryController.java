package org.example.orderservice.query.rest;


import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.example.orderservice.query.FindOrderQuery;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderQueryController {

    private final QueryGateway queryGateway;

    public OrderQueryController(QueryGateway queryGateway) {
        this.queryGateway = queryGateway;
    }

    @GetMapping
    public List<OrderRestModel> getOrders() {
        FindOrderQuery findOrderQuery = new FindOrderQuery("1");

        List<OrderRestModel> orders = queryGateway
                .query(findOrderQuery, ResponseTypes.multipleInstancesOf(OrderRestModel.class))
                .join();

        return orders;
    }
}
