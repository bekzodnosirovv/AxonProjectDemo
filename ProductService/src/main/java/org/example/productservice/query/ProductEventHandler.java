package org.example.productservice.query;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.ResetHandler;
import org.example.core.events.ProductReservationCancelledEvent;
import org.example.core.events.ProductReservedEvent;
import org.example.productservice.core.data.ProductEntity;
import org.example.productservice.core.data.ProductRepository;
import org.example.productservice.core.events.ProductCreateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Component
@ProcessingGroup("product-group")
public class ProductEventHandler {

    private final ProductRepository productRepository;

    private final Logger LOGGER = LoggerFactory.getLogger(ProductEventHandler.class);

    public ProductEventHandler(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @ExceptionHandler(Exception.class)
    public void handle(Exception exception) throws Exception {
        throw exception;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public void handle(IllegalArgumentException exception) {
        // Log error message
    }

    @EventHandler
    public void on(ProductCreateEvent event) {

        ProductEntity productEntity = new ProductEntity();
        BeanUtils.copyProperties(event, productEntity);
        try {
            productRepository.save(productEntity);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        // if (true) throw new Exception("Forcing exception in the Event Handler class");
    }

    @EventHandler
    public void on(ProductReservedEvent event) throws Exception {
        ProductEntity productEntity = productRepository.findByProductId(event.getProductId());
        productEntity.setQuantity(productEntity.getQuantity() - event.getQuantity());
        productRepository.save(productEntity);

        LOGGER.info("ProductReservedEvent is called for productId: {} and orderId: {}",
                event.getProductId(), event.getOrderId());
    }

    @EventHandler
    public void on(ProductReservationCancelledEvent event) throws Exception {
        ProductEntity productEntity = productRepository.findByProductId(event.getProductId());
        productEntity.setQuantity(productEntity.getQuantity() + event.getQuantity());
        productRepository.save(productEntity);
    }

    @ResetHandler
    public void reset() {
        productRepository.deleteAll();
    }

}
