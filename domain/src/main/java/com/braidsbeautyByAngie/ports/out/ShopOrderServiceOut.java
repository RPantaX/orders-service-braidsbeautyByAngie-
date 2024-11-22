package com.braidsbeautyByAngie.ports.out;

import com.braidsbeautyByAngie.aggregates.dto.ShopOrderDTO;
import com.braidsbeautyByAngie.aggregates.request.RequestShopOrder;
import com.braidsbeautyByAngie.aggregates.response.ResponseListPageableShopOrder;

import java.math.BigDecimal;

public interface ShopOrderServiceOut {
    void rejectShopOrderOut(Long orderId);
    void aprovedShopOrderOut(Long orderId, BigDecimal paymentTotalPrice, boolean isProduct, boolean isService);
    ShopOrderDTO createShopOrderOut(RequestShopOrder requestShopOrder);
    ResponseListPageableShopOrder getShopOrderListOut(int pageNumber, int pageSize, String orderBy, String sortDir);
}
