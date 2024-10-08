package org.example.paymentservice.events;

import org.axonframework.eventhandling.EventHandler;
import org.example.paymentservice.core.data.PaymentEntity;
import org.example.paymentservice.core.data.PaymentRepository;
import org.example.core.events.PaymentProcessedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventsHandler {

    private final Logger LOGGER = LoggerFactory.getLogger(PaymentEventsHandler.class);

    private final PaymentRepository paymentRepository;

    public PaymentEventsHandler(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @EventHandler
    public void on(PaymentProcessedEvent event) {
        LOGGER.info("Payment processed event: {}", event);

        PaymentEntity paymentEntity = new PaymentEntity(event.getPaymentId(), event.getOrderId());

        paymentRepository.save(paymentEntity);

    }
}
