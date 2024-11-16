package com.braidsbeautyByAngie.aggregates.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class RequestAdress {
    private String adressStreet;
    private String adressCity;
    private String adressState;
    private String adressCountry;
    private String adressPostalCode;
}
