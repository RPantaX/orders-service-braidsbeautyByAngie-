package com.braidsbeautyByAngie.ports.out;

import com.braidsbeautyByAngie.aggregates.dto.ShopOrderHistoryDTO;

import java.util.List;

public interface ShopOrderHistoryServiceOut {
    void addOut(Long orderId, String orderStatus);

    List<ShopOrderHistoryDTO> findByOrderIdOut(Long orderId);
}
