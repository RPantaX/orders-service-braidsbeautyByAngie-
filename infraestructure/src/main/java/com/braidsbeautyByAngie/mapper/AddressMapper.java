package com.braidsbeautyByAngie.mapper;

import com.braidsbeautyByAngie.aggregates.dto.AddressDTO;
import com.braidsbeautyByAngie.entity.AddressEntity;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class AddressMapper {
    private static final ModelMapper modelMapper = new ModelMapper();

    public AddressEntity convertToEntity(AddressDTO addressDTO) {
        return modelMapper.map(addressDTO, AddressEntity.class);
    }

    public AddressDTO addressDTO (AddressEntity addressEntity) {
        return modelMapper.map(addressEntity, AddressDTO.class);
    }
}
