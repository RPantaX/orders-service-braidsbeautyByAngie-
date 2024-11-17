package com.braidsbeautyByAngie.adapters;

import com.braidsbeautyByAngie.aggregates.request.RequestShopOrder;
import com.braidsbeautyByAngie.aggregates.response.ResponseListPageableShopOrder;
import com.braidsbeautyByAngie.ports.out.ShopOrderServiceOut;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShopOrderServiceAdapter implements ShopOrderServiceOut {
    @Override
    public void rejectShopOrderOut(Long orderId) {
        
    }

    @Override
    public void aprovedShopOrderOut(Long orderId) {

    }

    @Override
    public void createShopOrderOut(RequestShopOrder requestShopOrder) {

    }

    @Override
    public ResponseListPageableShopOrder getShopOrderListOut(int pageNumber, int pageSize, String orderBy, String sortDir) {
        return null;
    }
}
