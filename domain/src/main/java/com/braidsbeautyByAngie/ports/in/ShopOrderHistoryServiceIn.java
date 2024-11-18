package com.braidsbeautyByAngie.ports.in;

import com.braidsbeautyByAngie.aggregates.dto.ShopOrderHistoryDTO;

import java.util.List;

public interface ShopOrderHistoryServiceIn {
    void addIn(Long orderId, String orderStatus);

    List<ShopOrderHistoryDTO> findByOrderIdIn(Long orderId);
}
