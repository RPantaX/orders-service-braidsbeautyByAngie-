package com.braidsbeautyByAngie.mapper;

import com.braidsbeautyByAngie.aggregates.dto.OrderLineDTO;
import com.braidsbeautyByAngie.entity.OrderLineEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class OrderLineMapper {
    private static final ModelMapper modelMapper = new ModelMapper();

    public OrderLineEntity mapToEntity(OrderLineEntity orderLineEntity) {
        return modelMapper.map(orderLineEntity, OrderLineEntity.class);
    }
    public OrderLineDTO mapToDTO(OrderLineDTO orderLineDTO) {
        return modelMapper.map(orderLineDTO, OrderLineDTO.class);
    }
}
