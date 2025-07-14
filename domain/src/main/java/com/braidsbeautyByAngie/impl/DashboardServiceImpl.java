package com.braidsbeautyByAngie.impl;

import com.braidsbeautyByAngie.aggregates.dto.DashboardSummaryDTO;
import com.braidsbeautyByAngie.aggregates.dto.SalesAnalyticsDTO;
import com.braidsbeautyByAngie.aggregates.dto.TodayTransactionDTO;
import com.braidsbeautyByAngie.aggregates.dto.TopProductDTO;
import com.braidsbeautyByAngie.ports.in.DashboardServiceIn;
import com.braidsbeautyByAngie.ports.out.DashboardServiceOut;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardServiceIn {
    private final DashboardServiceOut dashboardServiceOut;
    @Override
    public DashboardSummaryDTO getDashboardSummaryIn() {
        return dashboardServiceOut.getDashboardSummaryOut();
    }

    @Override
    public List<SalesAnalyticsDTO> getSalesAnalyticsIn(String type, String period, LocalDate startDate, LocalDate endDate) {
        return dashboardServiceOut.getSalesAnalyticsOut(type, period, startDate, endDate);
    }

    @Override
    public List<TodayTransactionDTO> getTodayTransactionsIn() {
        return dashboardServiceOut.getTodayTransactionsOut();
    }

    @Override
    public List<TopProductDTO> getTopProductsIn(String period) {
        return dashboardServiceOut.getTopProductsOut(period);
    }
}
