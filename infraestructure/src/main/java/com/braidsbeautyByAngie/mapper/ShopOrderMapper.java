package com.braidsbeautyByAngie.mapper;

import com.braidsbeautyByAngie.aggregates.dto.ShopOrderDTO;
import com.braidsbeautyByAngie.entity.ShopOrderEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class ShopOrderMapper {
    private static final ModelMapper modelMapper = new ModelMapper();

    public ShopOrderEntity mapShopOrderDTOToShopOrderEntity(ShopOrderDTO shopOrderDTO) {
        return modelMapper.map(shopOrderDTO, ShopOrderEntity.class);
    }
    public ShopOrderDTO mapShopOrderEntityToShopOrderDTO(ShopOrderEntity shopOrderEntity) {
        return modelMapper.map(shopOrderEntity, ShopOrderDTO.class);
    }
}
