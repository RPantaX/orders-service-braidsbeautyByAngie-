package com.braidsbeautyByAngie.aggregates.response;

import java.util.List;

public class ResponseListPageableShopOrder {
    private List<ResponseShopOrder> responseShopOrderList;
    private int pageNumber;
    private int pageSize;
    private int totalPages;
    private long totalElements;
    private boolean end;
}
