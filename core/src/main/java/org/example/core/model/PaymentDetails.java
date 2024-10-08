package org.example.core.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentDetails {
    private final String name;
    private final String cartNumber;
    private final Integer validUntilMonth;
    private final Integer validUntilYear;
    private final String cvv;
}
