package com.braidsbeautyByAngie.aggregates.response.rest.reservations;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ResponseReservationDetail {
    private Long reservationId;
    private String reservationState;
    private BigDecimal reservationTotalPrice;
    private List<ResponseWorkServiceDetail> responseWorkServiceDetails;
}
