package org.example.paymentservice.core.data;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payments")
public class PaymentEntity {

    @Id
    private String paymentId;
    private String orderId;
}
