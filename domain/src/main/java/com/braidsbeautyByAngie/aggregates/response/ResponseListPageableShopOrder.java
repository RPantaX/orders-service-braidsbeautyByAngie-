package com.braidsbeautyByAngie.aggregates.response;

import lombok.*;

import java.util.List;
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ResponseListPageableShopOrder {
    private List<ResponseShopOrder> responseShopOrderList;
    private int pageNumber;
    private int pageSize;
    private int totalPages;
    private long totalElements;
    private boolean end;
}
