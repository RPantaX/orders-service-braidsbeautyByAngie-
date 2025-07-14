package com.braidsbeautyByAngie.adapters;

import com.braidsbeautyByAngie.aggregates.dto.DashboardSummaryDTO;
import com.braidsbeautyByAngie.aggregates.dto.SalesAnalyticsDTO;
import com.braidsbeautyByAngie.aggregates.dto.TodayTransactionDTO;
import com.braidsbeautyByAngie.aggregates.dto.TopProductDTO;
import com.braidsbeautyByAngie.aggregates.response.rest.products.ResponseProductItemDetail;
import com.braidsbeautyByAngie.aggregates.types.ShopOrderStatusEnum;
import com.braidsbeautyByAngie.entity.ShopOrderEntity;
import com.braidsbeautyByAngie.ports.out.DashboardServiceOut;
import com.braidsbeautyByAngie.repository.OrderLineRepository;
import com.braidsbeautyByAngie.repository.ShopOrderRepository;
import com.braidsbeautyByAngie.rest.RestProductsAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardServiceAdapter implements DashboardServiceOut {

    private final ShopOrderRepository shopOrderRepository;
    private final OrderLineRepository orderLineRepository;
    private final RestProductsAdapter restProductsAdapter;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public DashboardSummaryDTO getDashboardSummaryOut() {
        log.info("Generating dashboard summary");

        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate endOfMonth = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());

        // Total sales this month
        BigDecimal totalSales = calculateTotalSales(startOfMonth, endOfMonth);

        // In-store orders count (assuming shipping method ID 1 is in-store)
        Integer inStoreOrders = countInStoreOrders(startOfMonth, endOfMonth);

        // Online orders percentage
        Double onlinePercentage = calculateOnlineOrdersPercentage(startOfMonth, endOfMonth);

        // Star products count (products sold more than average)
        Integer starProducts = countStarProducts();

        return DashboardSummaryDTO.builder()
                .totalSalesThisMonth(totalSales)
                .inStoreOrdersCount(inStoreOrders)
                .onlineOrdersPercentage(onlinePercentage)
                .starProductsCount(starProducts)
                .build();
    }

    @Override
    public List<SalesAnalyticsDTO> getSalesAnalyticsOut(String type, String period, LocalDate startDate, LocalDate endDate) {
        log.info("Generating sales analytics for type: {}, period: {}", type, period);

        List<SalesAnalyticsDTO> analytics = new ArrayList<>();

        if (startDate != null && endDate != null) {
            // Custom date range
            analytics = generateAnalyticsForDateRange(type, startDate, endDate);
        } else {
            switch (period.toUpperCase()) {
                case "WEEKLY":
                    analytics = generateWeeklyAnalytics(type);
                    break;
                case "MONTHLY":
                    analytics = generateMonthlyAnalytics(type);
                    break;
                case "YEARLY":
                    analytics = generateYearlyAnalytics(type);
                    break;
                default:
                    analytics = generateMonthlyAnalytics(type);
            }
        }

        return analytics;
    }

    @Override
    public List<TodayTransactionDTO> getTodayTransactionsOut() {
        log.info("Fetching today's transactions");

        LocalDateTime startOfDay = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        String jpql = """
            SELECT so FROM ShopOrderEntity so 
            WHERE so.shopOrderDate BETWEEN :startOfDay AND :endOfDay 
            ORDER BY so.shopOrderDate DESC
            """;

        List<ShopOrderEntity> todayOrders = entityManager.createQuery(jpql, ShopOrderEntity.class)
                .setParameter("startOfDay", java.sql.Timestamp.valueOf(startOfDay))
                .setParameter("endOfDay", java.sql.Timestamp.valueOf(endOfDay))
                .getResultList();

        return todayOrders.stream()
                .map(this::mapToTodayTransactionDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TopProductDTO> getTopProductsOut(String period) {
        log.info("Fetching top products for period: {}", period);

        LocalDate startDate = calculateStartDateByPeriod(period);
        LocalDate endDate = LocalDate.now();

        String nativeQuery = """
            SELECT 
                ol.product_item_id,
                SUM(ol.order_line_quantity) as total_sold
            FROM order_line ol
            INNER JOIN shop_order so ON ol.shop_order_id = so.shop_order_id
            WHERE ol.product_item_id IS NOT NULL
            AND so.shop_order_date BETWEEN :startDate AND :endDate
            AND so.shop_order_status = 'APPROVED'
            GROUP BY ol.product_item_id
            ORDER BY total_sold DESC
            LIMIT 10
            """;

        Query query = entityManager.createNativeQuery(nativeQuery);
        query.setParameter("startDate", java.sql.Date.valueOf(startDate));
        query.setParameter("endDate", java.sql.Date.valueOf(endDate));

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();

        return results.stream()
                .map(row -> {
                    Long productItemId = ((Number) row[0]).longValue();
                    Long totalSold = ((Number) row[1]).longValue();

                    try {
                        // Fetch product details from product service
                        List<ResponseProductItemDetail> productDetails = (List<ResponseProductItemDetail>)restProductsAdapter.listItemProductsByIds(List.of(productItemId)).getData();
                        var productDetail =  productDetails
                                .stream().findFirst().orElse(null);

                        if (productDetail != null) {
                            return TopProductDTO.builder()
                                    .productImage(productDetail.getProductItemImage())
                                    .productName(productDetail.getProductItemSKU())
                                    .category(productDetail.getResponseCategory() != null ?
                                            productDetail.getResponseCategory().getProductCategoryName() : "Sin categoría")
                                    .price(productDetail.getProductItemPrice())
                                    .totalSold(totalSold)
                                    .build();
                        }
                    } catch (Exception e) {
                        log.error("Error fetching product details for ID: {}", productItemId, e);
                    }

                    return TopProductDTO.builder()
                            .productImage("default-image.jpg")
                            .productName("Producto #" + productItemId)
                            .category("Sin categoría")
                            .price(BigDecimal.ZERO)
                            .totalSold(totalSold)
                            .build();
                })
                .collect(Collectors.toList());
    }

    // Private helper methods
    private BigDecimal calculateTotalSales(LocalDate startDate, LocalDate endDate) {
        String jpql = """
            SELECT COALESCE(SUM(so.shopOrderTotal), 0) FROM ShopOrderEntity so 
            WHERE DATE(so.shopOrderDate) BETWEEN :startDate AND :endDate 
            AND so.shopOrderStatus = :status
            """;

        Number result = (Number) entityManager.createQuery(jpql)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .setParameter("status", ShopOrderStatusEnum.APPROVED)
                .getSingleResult();

        return result != null ? new BigDecimal(result.toString()) : BigDecimal.ZERO;
    }

    private Integer countInStoreOrders(LocalDate startDate, LocalDate endDate) {
        String jpql = """
            SELECT COUNT(so) FROM ShopOrderEntity so 
            WHERE DATE(so.shopOrderDate) BETWEEN :startDate AND :endDate 
            AND so.shoppingMethodEntity.shoppingMethodId = 1
            """;

        Number result = (Number) entityManager.createQuery(jpql)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getSingleResult();

        return result != null ? result.intValue() : 0;
    }

    private Double calculateOnlineOrdersPercentage(LocalDate startDate, LocalDate endDate) {
        String totalOrdersJpql = """
            SELECT COUNT(so) FROM ShopOrderEntity so 
            WHERE DATE(so.shopOrderDate) BETWEEN :startDate AND :endDate
            """;

        String onlineOrdersJpql = """
            SELECT COUNT(so) FROM ShopOrderEntity so 
            WHERE DATE(so.shopOrderDate) BETWEEN :startDate AND :endDate 
            AND so.shoppingMethodEntity.shoppingMethodId != 1
            """;

        Number totalOrders = (Number) entityManager.createQuery(totalOrdersJpql)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getSingleResult();

        Number onlineOrders = (Number) entityManager.createQuery(onlineOrdersJpql)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getSingleResult();

        if (totalOrders.intValue() == 0) return 0.0;

        return (onlineOrders.doubleValue() / totalOrders.doubleValue()) * 100;
    }

    private Integer countStarProducts() {
        // Products that sold more than average
        String avgQuery = """
            SELECT AVG(total_sold) FROM (
                SELECT SUM(ol.order_line_quantity) as total_sold
                FROM order_line ol
                WHERE ol.product_item_id IS NOT NULL
                GROUP BY ol.product_item_id
            ) as subquery
            """;

        Number avgSold = (Number) entityManager.createNativeQuery(avgQuery).getSingleResult();

        if (avgSold == null) return 0;

        String countQuery = """
            SELECT COUNT(*) FROM (
                SELECT ol.product_item_id
                FROM order_line ol
                WHERE ol.product_item_id IS NOT NULL
                GROUP BY ol.product_item_id
                HAVING SUM(ol.order_line_quantity) > :avgSold
            ) as star_products
            """;

        Number result = (Number) entityManager.createNativeQuery(countQuery)
                .setParameter("avgSold", avgSold)
                .getSingleResult();

        return result != null ? result.intValue() : 0;
    }

    private List<SalesAnalyticsDTO> generateMonthlyAnalytics(String type) {
        List<SalesAnalyticsDTO> analytics = new ArrayList<>();
        LocalDate now = LocalDate.now();

        for (int i = 11; i >= 0; i--) {
            YearMonth yearMonth = YearMonth.from(now.minusMonths(i));
            LocalDate startOfMonth = yearMonth.atDay(1);
            LocalDate endOfMonth = yearMonth.atEndOfMonth();

            SalesAnalyticsDTO dto = calculateAnalyticsForPeriod(type, startOfMonth, endOfMonth, yearMonth.getMonth().name().substring(0, 3));
            analytics.add(dto);
        }

        return analytics;
    }

    private List<SalesAnalyticsDTO> generateWeeklyAnalytics(String type) {
        List<SalesAnalyticsDTO> analytics = new ArrayList<>();
        LocalDate now = LocalDate.now();

        for (int i = 11; i >= 0; i--) {
            LocalDate weekStart = now.minusWeeks(i).minusDays(now.minusWeeks(i).getDayOfWeek().getValue() - 1);
            LocalDate weekEnd = weekStart.plusDays(6);

            SalesAnalyticsDTO dto = calculateAnalyticsForPeriod(type, weekStart, weekEnd, "Week " + (12 - i));
            analytics.add(dto);
        }

        return analytics;
    }

    private List<SalesAnalyticsDTO> generateYearlyAnalytics(String type) {
        List<SalesAnalyticsDTO> analytics = new ArrayList<>();
        LocalDate now = LocalDate.now();

        for (int i = 4; i >= 0; i--) {
            LocalDate yearStart = LocalDate.of(now.getYear() - i, 1, 1);
            LocalDate yearEnd = LocalDate.of(now.getYear() - i, 12, 31);

            SalesAnalyticsDTO dto = calculateAnalyticsForPeriod(type, yearStart, yearEnd, String.valueOf(now.getYear() - i));
            analytics.add(dto);
        }

        return analytics;
    }

    private List<SalesAnalyticsDTO> generateAnalyticsForDateRange(String type, LocalDate startDate, LocalDate endDate) {
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        List<SalesAnalyticsDTO> analytics = new ArrayList<>();

        if (daysBetween <= 31) {
            // Daily breakdown
            LocalDate current = startDate;
            while (!current.isAfter(endDate)) {
                SalesAnalyticsDTO dto = calculateAnalyticsForPeriod(type, current, current, current.toString());
                analytics.add(dto);
                current = current.plusDays(1);
            }
        } else {
            // Monthly breakdown
            YearMonth start = YearMonth.from(startDate);
            YearMonth end = YearMonth.from(endDate);

            YearMonth current = start;
            while (!current.isAfter(end)) {
                LocalDate monthStart = current.atDay(1);
                LocalDate monthEnd = current.atEndOfMonth();

                if (monthStart.isBefore(startDate)) monthStart = startDate;
                if (monthEnd.isAfter(endDate)) monthEnd = endDate;

                SalesAnalyticsDTO dto = calculateAnalyticsForPeriod(type, monthStart, monthEnd, current.getMonth().name().substring(0, 3));
                analytics.add(dto);
                current = current.plusMonths(1);
            }
        }

        return analytics;
    }

    private SalesAnalyticsDTO calculateAnalyticsForPeriod(String type, LocalDate startDate, LocalDate endDate, String periodName) {
        String productOrdersQuery = """
            SELECT COUNT(DISTINCT so.shop_order_id) FROM shop_order so
            INNER JOIN order_line ol ON so.shop_order_id = ol.shop_order_id
            WHERE DATE(so.shop_order_date) BETWEEN :startDate AND :endDate
            AND ol.product_item_id IS NOT NULL
            """;

        String serviceOrdersQuery = """
            SELECT COUNT(DISTINCT so.shop_order_id) FROM shop_order so
            INNER JOIN order_line ol ON so.shop_order_id = ol.shop_order_id
            WHERE DATE(so.shop_order_date) BETWEEN :startDate AND :endDate
            AND ol.reservation_id IS NOT NULL
            """;

        String totalOrdersQuery = """
            SELECT COUNT(so.shop_order_id) FROM shop_order so
            WHERE DATE(so.shop_order_date) BETWEEN :startDate AND :endDate
            """;

        Number productOrders = (Number) entityManager.createNativeQuery(productOrdersQuery)
                .setParameter("startDate", java.sql.Date.valueOf(startDate))
                .setParameter("endDate", java.sql.Date.valueOf(endDate))
                .getSingleResult();

        Number serviceOrders = (Number) entityManager.createNativeQuery(serviceOrdersQuery)
                .setParameter("startDate", java.sql.Date.valueOf(startDate))
                .setParameter("endDate", java.sql.Date.valueOf(endDate))
                .getSingleResult();

        Number totalOrders = (Number) entityManager.createNativeQuery(totalOrdersQuery)
                .setParameter("startDate", java.sql.Date.valueOf(startDate))
                .setParameter("endDate", java.sql.Date.valueOf(endDate))
                .getSingleResult();

        return SalesAnalyticsDTO.builder()
                .period(periodName)
                .productOrders(productOrders != null ? productOrders.longValue() : 0L)
                .serviceOrders(serviceOrders != null ? serviceOrders.longValue() : 0L)
                .totalOrders(totalOrders != null ? totalOrders.longValue() : 0L)
                .build();
    }

    private TodayTransactionDTO mapToTodayTransactionDTO(ShopOrderEntity order) {
        String orderType = determineOrderType(order);
        String customerName = "Customer #" + order.getUserId(); // You might want to fetch actual customer name

        return TodayTransactionDTO.builder()
                .orderId("#" + order.getShopOrderId())
                .customerName(customerName)
                .orderType(orderType)
                .orderDate(order.getShopOrderDate().toLocalDateTime())
                .status(order.getShopOrderStatus().name())
                .amount(order.getShopOrderTotal() != null ? order.getShopOrderTotal() : BigDecimal.ZERO)
                .build();
    }

    private String determineOrderType(ShopOrderEntity order) {
        boolean hasProducts = order.getOrderLineEntities().stream()
                .anyMatch(ol -> ol.getProductItemId() != null);
        boolean hasServices = order.getOrderLineEntities().stream()
                .anyMatch(ol -> ol.getReservationId() != null);

        if (hasProducts && hasServices) return "Mixed";
        if (hasProducts) return "Product";
        if (hasServices) return "Service";
        return "Unknown";
    }

    private LocalDate calculateStartDateByPeriod(String period) {
        LocalDate now = LocalDate.now();

        return switch (period.toUpperCase()) {
            case "WEEKLY" -> now.minusWeeks(1);
            case "MONTHLY" -> now.minusMonths(1);
            case "YEARLY" -> now.minusYears(1);
            default -> now.minusMonths(1);
        };
    }
}
