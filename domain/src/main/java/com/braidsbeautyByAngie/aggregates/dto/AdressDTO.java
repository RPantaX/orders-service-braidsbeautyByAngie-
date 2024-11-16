package com.braidsbeautyByAngie.aggregates.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AdressDTO {
    private Long adressId;
    private String adressStreet;
    private String adressCity;
    private String adressState;
    private String adressPostalCode;
    private String adressCountry;
}
