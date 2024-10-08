package org.example.productservice.query;

import org.axonframework.queryhandling.QueryHandler;
import org.example.productservice.core.data.ProductEntity;
import org.example.productservice.core.data.ProductRepository;
import org.example.productservice.query.rest.ProductRestModel;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProductQueryHandler {

    private final ProductRepository productRepository;

    public ProductQueryHandler(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @QueryHandler
    public List<ProductRestModel> findProducts(FindProductQuery query) {

        List<ProductEntity> products = productRepository.findAll();

        List<ProductRestModel> productRestModels = new ArrayList<>();

        for (ProductEntity productEntity : products) {
            ProductRestModel productRestModel = new ProductRestModel();
            BeanUtils.copyProperties(productEntity, productRestModel);
            productRestModels.add(productRestModel);
        }
        return productRestModels;
    }
}
