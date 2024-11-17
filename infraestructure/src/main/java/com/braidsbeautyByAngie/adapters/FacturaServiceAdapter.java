package com.braidsbeautyByAngie.adapters;

import com.braidsbeautyByAngie.aggregates.dto.FacturaDTO;
import com.braidsbeautyByAngie.aggregates.response.ResponseFactura;
import com.braidsbeautyByAngie.aggregates.response.ResponseListPageableFactura;
import com.braidsbeautyByAngie.ports.out.FacturaServiceOut;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FacturaServiceAdapter implements FacturaServiceOut {
    @Override
    public FacturaDTO createFacturaOut(FacturaDTO facturaDTO) {
        return null;
    }

    @Override
    public ResponseListPageableFactura getFacturaListOut(int pageNumber, int pageSize, String orderBy, String sortDir) {
        return null;
    }

    @Override
    public ResponseFactura getFacturaByIdOut(Long id) {
        return null;
    }
}
