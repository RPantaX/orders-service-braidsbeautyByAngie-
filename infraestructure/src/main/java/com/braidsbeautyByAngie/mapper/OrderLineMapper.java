package com.braidsbeautyByAngie.mapper;

import com.braidsbeautyByAngie.aggregates.dto.OrderLineDTO;
import com.braidsbeautyByAngie.entity.OrderLineEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class OrderLineMapper {
    private static final ModelMapper modelMapper = new ModelMapper();

    public OrderLineEntity mapToEntity(OrderLineDTO orderLineDTO) {
        return modelMapper.map(orderLineDTO, OrderLineEntity.class);
    }
    public OrderLineDTO mapToDTO(OrderLineEntity orderLineEntity) {
        return modelMapper.map(orderLineEntity, OrderLineDTO.class);
    }
}
