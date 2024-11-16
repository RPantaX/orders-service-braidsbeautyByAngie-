package com.braidsbeautyByAngie.impl;

import com.braidsbeautyByAngie.aggregates.dto.FacturaDTO;
import com.braidsbeautyByAngie.aggregates.response.ResponseFactura;
import com.braidsbeautyByAngie.aggregates.response.ResponseListPageableFactura;
import com.braidsbeautyByAngie.ports.in.FacturaServicIn;
import com.braidsbeautyByAngie.ports.out.FacturaServiceOut;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FacturaServiceImpl implements FacturaServicIn {

    private final FacturaServiceOut facturaServiceOut;

    @Override
    public FacturaDTO createFacturaIn(FacturaDTO facturaDTO) {
        return facturaServiceOut.createFacturaOut(facturaDTO);
    }

    @Override
    public ResponseListPageableFactura getFacturaListIn(int pageNumber, int pageSize, String orderBy, String sortDir) {
        return facturaServiceOut.getFacturaListOut(pageNumber, pageSize, orderBy, sortDir);
    }

    @Override
    public ResponseFactura getFacturaByIdIn(Long id) {
        return facturaServiceOut.getFacturaByIdOut(id);
    }
}
