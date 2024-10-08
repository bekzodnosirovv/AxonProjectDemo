package org.example.core.commands;

import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;
import org.example.core.model.PaymentDetails;

@Data
@Builder
public class ProcessPaymentCommand {
    @TargetAggregateIdentifier
    private final String paymentId;
    private final String orderId;
    private final PaymentDetails paymentDetails;
}
