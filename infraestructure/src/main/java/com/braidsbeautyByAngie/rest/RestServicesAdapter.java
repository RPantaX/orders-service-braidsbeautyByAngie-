package com.braidsbeautyByAngie.rest;

import com.braidsbeautyByAngie.aggregates.response.rest.reservations.ResponseReservationDetail;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "reservation-service")
public interface RestServicesAdapter {

    @GetMapping("/v1/reservation-service/reservation/{reservationId}")
    ResponseReservationDetail listReservationById(@PathVariable(name = "reservationId") Long reservationId);
}
