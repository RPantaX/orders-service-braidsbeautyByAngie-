package com.braidsbeautyByAngie.aggregates.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AddressDTO {
    private Long addressId;
    private String addressStreet;
    private String addressCity;
    private String addressState;
    private String addressPostalCode;
    private String addressCountry;
}
