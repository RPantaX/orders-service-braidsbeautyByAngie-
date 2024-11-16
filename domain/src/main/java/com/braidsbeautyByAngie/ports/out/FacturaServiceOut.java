package com.braidsbeautyByAngie.ports.out;

import com.braidsbeautyByAngie.aggregates.dto.FacturaDTO;
import com.braidsbeautyByAngie.aggregates.response.ResponseFactura;
import com.braidsbeautyByAngie.aggregates.response.ResponseListPageableFactura;

public interface FacturaServiceOut {
    FacturaDTO createFacturaOut(FacturaDTO facturaDTO);
    ResponseListPageableFactura getFacturaListOut(int pageNumber, int pageSize, String orderBy, String sortDir);
    ResponseFactura getFacturaByIdOut(Long id);
}
