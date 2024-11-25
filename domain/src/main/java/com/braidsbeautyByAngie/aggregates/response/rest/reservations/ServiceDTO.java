package com.braidsbeautyByAngie.aggregates.response.rest.reservations;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalTime;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ServiceDTO {
    private Long serviceId;
    private String serviceName;
    private String serviceDescription;
    private BigDecimal servicePrice;
    private String serviceImage;
    private LocalTime durationTimeAprox;
}
