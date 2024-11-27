package com.braidsbeautyByAngie.adapters;

import com.braidsbeautyByAngie.aggregates.dto.ShopOrderHistoryDTO;
import com.braidsbeautyByAngie.aggregates.types.ShopOrderHistoryStatusEnum;
import com.braidsbeautyByAngie.entity.ShopOrderHistoryEntity;
import com.braidsbeautyByAngie.mapper.ShopOrderHistoryMapper;
import com.braidsbeautyByAngie.ports.out.ShopOrderHistoryServiceOut;
import com.braidsbeautyByAngie.repository.ShopOrderHistoryRepository;
import com.braidsbeautybyangie.sagapatternspringboot.aggregates.AppExceptions.AppExceptionNotFound;
import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
@Slf4j
public class ShopOrderHistoryAdapter implements ShopOrderHistoryServiceOut {
    private final ShopOrderHistoryRepository shopOrderHistoryRepository;
    private final ShopOrderHistoryMapper shopOrderHistoryMapper;

    @Override
    public void addOut(Long orderId, ShopOrderHistoryStatusEnum orderStatus) {
        log.info("Adding order history: orderId={}, status={}", orderId, orderStatus);

        if (orderId == null || orderStatus == null) {
            log.warn("Invalid input: orderId or orderStatus is null. orderId={}, status={}", orderId, orderStatus);
            throw new AppExceptionNotFound("Order ID and status must not be null");
        }

        ShopOrderHistoryEntity shopOrderHistoryEntity = buildShopOrderHistoryEntity(orderId, orderStatus);

        try {
            shopOrderHistoryRepository.save(shopOrderHistoryEntity);
            log.info("Order history successfully saved: orderId={}, status={}", orderId, orderStatus);
        } catch (Exception e) {
            log.error("Error saving order history: orderId={}, status={}", orderId, orderStatus, e);
            throw e; // Rethrow exception for higher-level handling
        }
    }

    @Override
    public List<ShopOrderHistoryDTO> findByOrderIdOut(Long orderId) {
        log.info("Fetching order history for orderId={}", orderId);
        if (orderId == null) {
            log.warn("Order ID is null. Cannot fetch order history.");
            throw new IllegalArgumentException("Order ID must not be null");
        }

        List<ShopOrderHistoryEntity> shopOrderHistoryEntities = shopOrderHistoryRepository.findByShopOrderId(orderId);
        if (shopOrderHistoryEntities.isEmpty()) {
            log.warn("No order history found for orderId={}", orderId);
            throw new AppExceptionNotFound("No order history found for orderId=" + orderId);
        }
        log.debug("Found {} order history entries for orderId={}", shopOrderHistoryEntities.size(), orderId);

        List<ShopOrderHistoryDTO> dtos = shopOrderHistoryEntities.stream()
                .map(shopOrderHistoryMapper::mapShopOrderHistoryEntityToDTO)
                .toList();

        log.info("Successfully mapped order history to DTOs for orderId={}", orderId);
        return dtos;
    }

    /**
     * Build a ShopOrderHistoryEntity.
     */
    private ShopOrderHistoryEntity buildShopOrderHistoryEntity(Long orderId, ShopOrderHistoryStatusEnum orderStatus) {
        log.debug("Building ShopOrderHistoryEntity: orderId={}, status={}", orderId, orderStatus);
        return ShopOrderHistoryEntity.builder()
                .shopOrderId(orderId)
                .createdAt(Constants.getTimestamp())
                .status(orderStatus)
                .build();
    }
}
