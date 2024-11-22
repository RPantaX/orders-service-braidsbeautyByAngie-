package com.braidsbeautyByAngie.impl;

import com.braidsbeautyByAngie.aggregates.dto.ShopOrderHistoryDTO;
import com.braidsbeautyByAngie.aggregates.types.ShopOrderHistoryStatusEnum;
import com.braidsbeautyByAngie.ports.in.ShopOrderHistoryServiceIn;
import com.braidsbeautyByAngie.ports.out.ShopOrderHistoryServiceOut;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShopOrderHistoryServiceImpl implements ShopOrderHistoryServiceIn {

    private final ShopOrderHistoryServiceOut shopOrderHistoryServiceOut;

    @Override
    public void addIn(Long orderId, ShopOrderHistoryStatusEnum orderStatus) {
        shopOrderHistoryServiceOut.addOut(orderId, orderStatus);
    }

    @Override
    public List<ShopOrderHistoryDTO> findByOrderIdIn(Long orderId) {
        return shopOrderHistoryServiceOut.findByOrderIdOut(orderId);
    }
}
