package com.braidsbeautyByAngie.impl;

import com.braidsbeautyByAngie.aggregates.dto.ShopOrderDTO;
import com.braidsbeautyByAngie.aggregates.request.RequestShopOrder;
import com.braidsbeautyByAngie.aggregates.response.ResponseListPageableShopOrder;
import com.braidsbeautyByAngie.ports.in.ShopOrderServiceIn;
import com.braidsbeautyByAngie.ports.out.ShopOrderServiceOut;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShopOrderServiceImpl implements ShopOrderServiceIn {

    private final ShopOrderServiceOut serviceOut;

    @Override
    public void rejectShopOrderIn(Long orderId) {
        serviceOut.rejectShopOrderOut(orderId);
    }

    @Override
    public void aprovedShopOrderIn(Long orderId) {
        serviceOut.aprovedShopOrderOut(orderId);
    }

    @Override
    public ShopOrderDTO createShopOrderIn(RequestShopOrder requestShopOrder) {
        return serviceOut.createShopOrderOut(requestShopOrder);
    }

    @Override
    public ResponseListPageableShopOrder getShopOrderListIn(int pageNumber, int pageSize, String orderBy, String sortDir) {
        return serviceOut.getShopOrderListOut(pageNumber, pageSize, orderBy, sortDir);
    }
}
