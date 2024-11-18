package com.braidsbeautyByAngie.mapper;

import com.braidsbeautyByAngie.aggregates.dto.ShopOrderHistoryDTO;
import com.braidsbeautyByAngie.entity.ShopOrderHistoryEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class ShopOrderHistoryMapper {
    private static final ModelMapper modelMapper = new ModelMapper();

    public ShopOrderHistoryDTO mapShopOrderHistoryEntityToDTO(ShopOrderHistoryEntity shopOrderHistoryEntity){
        return modelMapper.map(shopOrderHistoryEntity, ShopOrderHistoryDTO.class);
    }
    public ShopOrderHistoryEntity mapShopOrderHistoryDTOToEntity(ShopOrderHistoryDTO shopOrderHistoryDTO){
        return modelMapper.map(shopOrderHistoryDTO, ShopOrderHistoryEntity.class);
    }
}
