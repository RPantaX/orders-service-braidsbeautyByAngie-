package com.braidsbeautyByAngie.mapper;

import com.braidsbeautyByAngie.aggregates.dto.ShopOrderDTO;
import com.braidsbeautyByAngie.entity.ShopOrderEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class ShoppingMethodMapper {
    private static final ModelMapper modelMapper = new ModelMapper();

    public ShopOrderDTO convertToShopOrderDTO(ShopOrderEntity shopOrder) {
        return modelMapper.map(shopOrder, ShopOrderDTO.class);
    }

    public ShopOrderEntity convertToShopOrderEntity(ShopOrderDTO shopOrderDTO) {
        return modelMapper.map(shopOrderDTO, ShopOrderEntity.class);
    }
}
