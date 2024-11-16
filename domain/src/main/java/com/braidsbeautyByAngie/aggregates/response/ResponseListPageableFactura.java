package com.braidsbeautyByAngie.aggregates.response;

import java.util.List;

public class ResponseListPageableFactura {
    private List<ResponseFactura> responseFacturaList;
    private int pageNumber;
    private int pageSize;
    private int totalPages;
    private long totalElements;
    private boolean end;
}
