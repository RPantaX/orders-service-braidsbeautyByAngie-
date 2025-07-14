package com.braidsbeautyByAngie.aggregates.dto;

// DashboardSummaryDTO.java

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryDTO {
    private BigDecimal totalSalesThisMonth;
    private Integer inStoreOrdersCount;
    private Double onlineOrdersPercentage;
    private Integer starProductsCount;
}

