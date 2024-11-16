package com.braidsbeautyByAngie.ports.in;

import com.braidsbeautyByAngie.aggregates.dto.FacturaDTO;
import com.braidsbeautyByAngie.aggregates.response.ResponseFactura;
import com.braidsbeautyByAngie.aggregates.response.ResponseListPageableFactura;

public interface FacturaServicIn {

    FacturaDTO createFacturaIn(FacturaDTO facturaDTO);
    ResponseListPageableFactura getFacturaListIn(int pageNumber, int pageSize, String orderBy, String sortDir);
    ResponseFactura getFacturaByIdIn(Long id);
}
