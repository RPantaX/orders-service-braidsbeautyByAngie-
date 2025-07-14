package com.braidsbeautyByAngie.controller;

import com.braidsbeautyByAngie.aggregates.dto.DashboardSummaryDTO;
import com.braidsbeautyByAngie.aggregates.dto.SalesAnalyticsDTO;
import com.braidsbeautyByAngie.aggregates.dto.TodayTransactionDTO;
import com.braidsbeautyByAngie.aggregates.dto.TopProductDTO;
import com.braidsbeautyByAngie.ports.in.DashboardServiceIn;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@OpenAPIDefinition(
        info = @Info(
                title = "API-DASHBOARD",
                version = "1.0",
                description = "Dashboard analytics management"
        )
)
@RestController
@RequestMapping("/v1/orders-service/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardServiceIn dashboardService;

    @Operation(summary = "Get dashboard summary cards")
    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryDTO> getDashboardSummary() {
        return ResponseEntity.ok(dashboardService.getDashboardSummaryIn());
    }

    @Operation(summary = "Get sales analytics chart data")
    @GetMapping("/analytics")
    public ResponseEntity<List<SalesAnalyticsDTO>> getSalesAnalytics(
            @RequestParam(defaultValue = "PRODUCT") String type,
            @RequestParam(defaultValue = "MONTHLY") String period,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(dashboardService.getSalesAnalyticsIn(type, period, startDate, endDate));
    }

    @Operation(summary = "Get today's transactions")
    @GetMapping("/transactions/today")
    public ResponseEntity<List<TodayTransactionDTO>> getTodayTransactions() {
        return ResponseEntity.ok(dashboardService.getTodayTransactionsIn());
    }

    @Operation(summary = "Get top selling products")
    @GetMapping("/top-products")
    public ResponseEntity<List<TopProductDTO>> getTopProducts(
            @RequestParam(defaultValue = "MONTHLY") String period) {
        return ResponseEntity.ok(dashboardService.getTopProductsIn(period));
    }
}