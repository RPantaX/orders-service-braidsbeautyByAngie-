package com.braidsbeautyByAngie.mapper;

import com.braidsbeautyByAngie.aggregates.dto.FacturaDTO;
import com.braidsbeautyByAngie.entity.FacturaEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class FacturaMapper {
    private static final ModelMapper modelMapper = new ModelMapper();

    public FacturaEntity mapToEntity(FacturaEntity facturaEntity) {
        return modelMapper.map(facturaEntity, FacturaEntity.class);
    }
    public FacturaDTO mapToDTO(FacturaDTO facturaDTO) {
        return modelMapper.map(facturaDTO, FacturaDTO.class);
    }
}
