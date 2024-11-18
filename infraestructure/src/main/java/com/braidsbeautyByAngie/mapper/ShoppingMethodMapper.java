package com.braidsbeautyByAngie.mapper;

import com.braidsbeautyByAngie.aggregates.dto.ShopOrderDTO;
import com.braidsbeautyByAngie.aggregates.dto.ShoppingMethodDTO;
import com.braidsbeautyByAngie.entity.ShopOrderEntity;
import com.braidsbeautyByAngie.entity.ShoppingMethodEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class ShoppingMethodMapper {
    private static final ModelMapper modelMapper = new ModelMapper();

    public ShoppingMethodDTO convertToShopOrderDTO(ShoppingMethodEntity shoppingMethodEntity) {
        return modelMapper.map(shoppingMethodEntity, ShoppingMethodDTO.class);
    }

    public ShoppingMethodEntity convertToShopOrderEntity(ShoppingMethodDTO shoppingMethodDTO) {
        return modelMapper.map(shoppingMethodDTO, ShoppingMethodEntity.class);
    }
}
