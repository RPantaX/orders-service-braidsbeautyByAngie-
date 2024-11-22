package com.braidsbeautyByAngie.adapters;

import com.braidsbeautyByAngie.aggregates.dto.ShopOrderHistoryDTO;
import com.braidsbeautyByAngie.aggregates.types.ShopOrderHistoryStatusEnum;
import com.braidsbeautyByAngie.entity.ShopOrderHistoryEntity;
import com.braidsbeautyByAngie.mapper.ShopOrderHistoryMapper;
import com.braidsbeautyByAngie.ports.out.ShopOrderHistoryServiceOut;
import com.braidsbeautyByAngie.repository.ShopOrderHistoryRepository;
import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShopOrderHistoryAdapter implements ShopOrderHistoryServiceOut {
    private final ShopOrderHistoryRepository shopOrderHistoryRepository;
    private final ShopOrderHistoryMapper shopOrderHistoryMapper;

    @Override
    public void addOut(Long orderId, ShopOrderHistoryStatusEnum orderStatus) {
        ShopOrderHistoryEntity shopOrderHistoryEntity = ShopOrderHistoryEntity.builder()
                .shopOrderId(orderId)
                .createdAt(Constants.getTimestamp())
                .status(orderStatus)
                .build();
        shopOrderHistoryRepository.save(shopOrderHistoryEntity);
    }

    @Override
    public List<ShopOrderHistoryDTO> findByOrderIdOut(Long orderId) {
        var shopOrderHistoryEntities = shopOrderHistoryRepository.findByShopOrderId(orderId);
        return shopOrderHistoryEntities.stream().map(shopOrderHistoryMapper::mapShopOrderHistoryEntityToDTO).toList();
    }
}
