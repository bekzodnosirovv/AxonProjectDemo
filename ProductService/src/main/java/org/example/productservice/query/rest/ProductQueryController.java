package org.example.productservice.query.rest;

import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.example.productservice.query.FindProductQuery;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductQueryController {

    private final QueryGateway queryGateway;

    public ProductQueryController(QueryGateway queryGateway) {
        this.queryGateway = queryGateway;
    }

    @GetMapping
    public List<ProductRestModel> getProducts() {

        FindProductQuery findProductQuery = new FindProductQuery();

        List<ProductRestModel> products = queryGateway
                .query(findProductQuery, ResponseTypes.multipleInstancesOf(ProductRestModel.class))
                .join();

        return products;
    }

}
