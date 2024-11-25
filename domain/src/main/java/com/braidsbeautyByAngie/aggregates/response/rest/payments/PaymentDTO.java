package com.braidsbeautyByAngie.aggregates.response.rest.payments;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalTime;
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PaymentDTO {
    private Long paymentId;
    private String paymentProvider;
    private BigInteger paymentAccountNumber;
    private LocalTime paymentExpirationDate;
    private boolean paymentIsDefault;
    private BigDecimal paymentTotalPrice;
    private Long userId;
    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;
    private Long shopOrderId;
}
