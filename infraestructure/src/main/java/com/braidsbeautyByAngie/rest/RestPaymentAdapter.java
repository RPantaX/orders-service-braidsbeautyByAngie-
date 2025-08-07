package com.braidsbeautyByAngie.rest;

import com.braidsbeautyByAngie.aggregates.response.rest.ApiResponse;
import com.braidsbeautyByAngie.aggregates.response.rest.payments.PaymentDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

//@CircuitBreaker(name = "payment-service")
//@Retry(name = "payment-service")
//@RateLimiter(name = "payment-service", fallbackMethod = "fallback")
//@TimeLimiter(name = "payment-service")
@FeignClient(name = "payment-service")
public interface RestPaymentAdapter {

    @GetMapping("/v1/payment-service/payment/{shopOrderId}")
    ApiResponse<PaymentDTO> getPaymentByShopOrderId(@PathVariable(name = "shopOrderId") Long shopOrderId);
}
