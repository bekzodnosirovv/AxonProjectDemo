package org.example.orderservice.saga;

import org.axonframework.commandhandling.CommandCallback;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.CommandResultMessage;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.deadline.DeadlineManager;
import org.axonframework.deadline.annotation.DeadlineHandler;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.axonframework.spring.stereotype.Saga;
import org.example.core.commands.CancelProductReservationCommand;
import org.example.core.commands.ProcessPaymentCommand;
import org.example.core.commands.ReserveProductCommand;
import org.example.core.events.PaymentProcessedEvent;
import org.example.core.events.ProductReservationCancelledEvent;
import org.example.core.events.ProductReservedEvent;
import org.example.core.model.User;
import org.example.core.query.FetchUserPaymentDetailsQuery;
import org.example.orderservice.command.commands.ApproveOrderCommand;
import org.example.orderservice.command.commands.RejectOrderCommand;
import org.example.orderservice.core.events.OrderApproveEvent;
import org.example.orderservice.core.events.OrderCreateEvent;
import org.example.orderservice.core.events.OrderRejectEvent;
import org.example.orderservice.core.model.OrderSummary;
import org.example.orderservice.query.FindOrderQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.UUID;


@Saga
public class OrderSaga {

    @Autowired
    private transient CommandGateway commandGateway;

    @Autowired
    private transient QueryGateway queryGateway;

    @Autowired
    private transient DeadlineManager deadlineManager;

    @Autowired
    private transient QueryUpdateEmitter queryUpdateEmitter;

    private String scheduleId;

    private final String PAYMENT_PROCESSING_TIMEOUT_DEADLINE = "payment-processing-deadline";

    private final Logger LOGGER = LoggerFactory.getLogger(OrderSaga.class);

    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderCreateEvent event) {

        ReserveProductCommand reserveProductCommand = ReserveProductCommand.builder()
                .orderId(event.getOrderId())
                .productId(event.getProductId())
                .quantity(event.getQuantity())
                .userId(event.getUserId())
                .build();

        LOGGER.info("OrderCreatedEvent handled for orderId: " + reserveProductCommand.getOrderId()
                + " and productId: " + reserveProductCommand.getProductId());

        commandGateway.send(reserveProductCommand, new CommandCallback<ReserveProductCommand, Object>() {
            @Override
            public CommandCallback<ReserveProductCommand, Object> wrap(CommandCallback<ReserveProductCommand, Object> wrappingCallback) {
                return CommandCallback.super.wrap(wrappingCallback);
            }

            @Override
            public void onResult(@Nonnull CommandMessage<? extends ReserveProductCommand> commandMessage, @Nonnull CommandResultMessage<?> commandResultMessage) {
                if (commandResultMessage.isExceptional()) {
                    LOGGER.error("Error Error Error Error Error Error");
                    RejectOrderCommand rejectOrderCommand = new RejectOrderCommand(event.getOrderId(),
                            commandResultMessage.exceptionResult().getMessage());
                    commandGateway.send(rejectOrderCommand);
                }
            }
        });
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(ProductReservedEvent event) {
        // user payment progress
        LOGGER.info("ProductReservedEvent is called for productId: " + event.getProductId() +
                " and orderId: " + event.getOrderId());

        FetchUserPaymentDetailsQuery fetchUserPaymentDetailsQuery = new FetchUserPaymentDetailsQuery(event.getUserId());

        User userPaymentsDetails = null;

        try {
            userPaymentsDetails = queryGateway.query(fetchUserPaymentDetailsQuery,
                    ResponseTypes.instanceOf(User.class)).join();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            cancelProductReservation(event, e.getMessage());
            return;
        }

        if (userPaymentsDetails == null) {
            //start compensating transaction
            cancelProductReservation(event, "Could not fetch user payment details");
            return;
        }

        LOGGER.info("Successfully fetched user payment details for user " + userPaymentsDetails.getFirstName());

        scheduleId = deadlineManager.schedule(Duration.of(10, ChronoUnit.SECONDS),
                PAYMENT_PROCESSING_TIMEOUT_DEADLINE, event);

//        if (true) return;

        ProcessPaymentCommand processPaymentCommand = ProcessPaymentCommand.builder()
                .orderId(event.getOrderId())
                .paymentDetails(userPaymentsDetails.getPaymentDetails())
                .paymentId(UUID.randomUUID().toString())
                .build();

        String result = null;

        try {
            result = commandGateway.sendAndWait(processPaymentCommand);
        } catch (Exception ex) {
            // start compensating transaction
            LOGGER.error(ex.getMessage());
            cancelProductReservation(event, ex.getMessage());
            return;
        }

        if (result == null) {
            // start compensating transaction
            LOGGER.info("ProcessPaymentCommand returned null");
            cancelProductReservation(event, "Could not process user payment with provided payment details");
        }

    }

    private void cancelProductReservation(ProductReservedEvent event, String reason) {

        canselDeadline();

        CancelProductReservationCommand cancelProductReservationCommand = CancelProductReservationCommand.builder()
                .orderId(event.getOrderId())
                .productId(event.getProductId())
                .userId(event.getUserId())
                .quantity(event.getQuantity())
                .reason(reason)
                .build();

        commandGateway.send(cancelProductReservationCommand);
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(PaymentProcessedEvent processedEvent) {
        canselDeadline();

        ApproveOrderCommand approveOrderCommand = new ApproveOrderCommand(processedEvent.getOrderId());
        commandGateway.send(approveOrderCommand);
    }

    private void canselDeadline() {
        if (scheduleId != null) {
            deadlineManager.cancelSchedule(PAYMENT_PROCESSING_TIMEOUT_DEADLINE, scheduleId);
            scheduleId = null;
        }
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderApproveEvent orderApproveEvent) {
        LOGGER.info("Order is approved. Order Saga is complete for orderId:  " + orderApproveEvent.getOrderId());
//        SagaLifecycle.end();
        queryUpdateEmitter.emit(FindOrderQuery.class, query -> true,
                new OrderSummary(orderApproveEvent.getOrderId(),
                        orderApproveEvent.getOrderStatus(),
                        ""));
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(ProductReservationCancelledEvent event) {
        // Create and Send a RejectOrderCommand

        RejectOrderCommand rejectOrderCommand = new RejectOrderCommand(event.getOrderId(), event.getReason());
        commandGateway.send(rejectOrderCommand);

    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderRejectEvent orderRejectEvent) {
        LOGGER.info("Successfully rejected order with id " + orderRejectEvent.getOrderId());
        queryUpdateEmitter.emit(FindOrderQuery.class, query -> true,
                new OrderSummary(orderRejectEvent.getOrderId(),
                        orderRejectEvent.getOrderStatus(),
                        orderRejectEvent.getReason()));

    }

    @DeadlineHandler(deadlineName = PAYMENT_PROCESSING_TIMEOUT_DEADLINE)
    public void handlePaymentDeadline(ProductReservedEvent event) {
        LOGGER.info("Payment processing deadline took place. Sending a compensating command to cancel the product reservation");
        cancelProductReservation(event, "Payment timeout");
    }
}
