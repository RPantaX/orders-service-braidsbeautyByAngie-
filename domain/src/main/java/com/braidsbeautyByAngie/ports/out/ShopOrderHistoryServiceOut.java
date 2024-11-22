package com.braidsbeautyByAngie.ports.out;

import com.braidsbeautyByAngie.aggregates.dto.ShopOrderHistoryDTO;
import com.braidsbeautyByAngie.aggregates.types.ShopOrderHistoryStatusEnum;

import java.util.List;

public interface ShopOrderHistoryServiceOut {
    void addOut(Long orderId, ShopOrderHistoryStatusEnum orderStatus);

    List<ShopOrderHistoryDTO> findByOrderIdOut(Long orderId);
}
