package com.braidsbeautyByAngie.rest;

import com.braidsbeautyByAngie.aggregates.response.rest.reservations.ResponseReservationDetail;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@CircuitBreaker(name = "reservation-service", fallbackMethod = "fallback")
@Retry(name = "reservation-service")
@TimeLimiter(name = "reservation-service")
@FeignClient(name = "reservation-service")
public interface RestServicesAdapter {

    @GetMapping("/v1/reservation-service/reservation/{reservationId}")
    ResponseReservationDetail listReservationById(@PathVariable(name = "reservationId") Long reservationId);
}
