package org.example.paymentservice.commands;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.example.core.commands.ProcessPaymentCommand;
import org.example.core.events.PaymentProcessedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aggregate(snapshotTriggerDefinition = "paymentSnapshotTriggerDefinition")
public class PaymentAggregate {

    @AggregateIdentifier
    private String paymentId;
    private String orderId;

    private final Logger LOGGER = LoggerFactory.getLogger(PaymentAggregate.class);

    public PaymentAggregate() {
    }

    @CommandHandler
    public PaymentAggregate(ProcessPaymentCommand command) {

        if (command.getPaymentId() == null) {
            LOGGER.error("Payment id is null");
            throw new IllegalArgumentException("Payment id is required");
        }
        if (command.getOrderId() == null) {
            LOGGER.error("Order id is null");
            throw new IllegalArgumentException("Order id is required");
        }
        if (command.getPaymentDetails() == null) {
            LOGGER.error("Payment details is null");
            throw new IllegalArgumentException("Payment details is required");
        }

        PaymentProcessedEvent event = PaymentProcessedEvent.builder()
                .paymentId(command.getPaymentId())
                .orderId(command.getOrderId())
                .build();

        AggregateLifecycle.apply(event);

    }

    @EventSourcingHandler
    public void on(PaymentProcessedEvent event) {
        this.paymentId = event.getPaymentId();
        this.orderId = event.getOrderId();
    }


}
