package com.braidsbeautyByAngie.ports.in;

import com.braidsbeautyByAngie.aggregates.dto.ShopOrderHistoryDTO;
import com.braidsbeautyByAngie.aggregates.types.ShopOrderHistoryStatusEnum;

import java.util.List;

public interface ShopOrderHistoryServiceIn {
    void addIn(Long orderId, ShopOrderHistoryStatusEnum orderStatus);

    List<ShopOrderHistoryDTO> findByOrderIdIn(Long orderId);
}
