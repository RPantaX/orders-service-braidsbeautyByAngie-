package com.braidsbeautyByAngie.ports.in;

import com.braidsbeautyByAngie.aggregates.dto.DashboardSummaryDTO;
import com.braidsbeautyByAngie.aggregates.dto.SalesAnalyticsDTO;
import com.braidsbeautyByAngie.aggregates.dto.TodayTransactionDTO;
import com.braidsbeautyByAngie.aggregates.dto.TopProductDTO;

import java.time.LocalDate;
import java.util.List;

public interface DashboardServiceIn {
    DashboardSummaryDTO getDashboardSummaryIn();
    List<SalesAnalyticsDTO> getSalesAnalyticsIn(String type, String period, LocalDate startDate, LocalDate endDate);
    List<TodayTransactionDTO> getTodayTransactionsIn();
    List<TopProductDTO> getTopProductsIn(String period);
}
