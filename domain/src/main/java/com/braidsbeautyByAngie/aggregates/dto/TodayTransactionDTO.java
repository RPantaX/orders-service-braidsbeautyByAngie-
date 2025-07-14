package com.braidsbeautyByAngie.aggregates.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodayTransactionDTO {
    private String orderId;
    private String customerName;
    private String orderType; // "Product", "Service", "Mixed"
    private LocalDateTime orderDate;
    private String status;
    private BigDecimal amount;
}
