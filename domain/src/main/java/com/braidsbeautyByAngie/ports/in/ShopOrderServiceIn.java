package com.braidsbeautyByAngie.ports.in;

import com.braidsbeautyByAngie.aggregates.request.RequestShopOrder;
import com.braidsbeautyByAngie.aggregates.response.ResponseListPageableShopOrder;

public interface ShopOrderServiceIn {

    void rejectShopOrderIn(Long orderId);
    void aprovedShopOrderIn(Long orderId);
    void createShopOrderIn(RequestShopOrder requestShopOrder);
    ResponseListPageableShopOrder getShopOrderListIn(int pageNumber, int pageSize, String orderBy, String sortDir);
}
