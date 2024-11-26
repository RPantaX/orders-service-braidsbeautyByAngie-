package com.braidsbeautyByAngie.rest;

import com.braidsbeautyByAngie.aggregates.response.rest.payments.PaymentDTO;
import com.braidsbeautybyangie.sagapatternspringboot.aggregates.AppExceptions.AppException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@CircuitBreaker(name = "payment-service", fallbackMethod = "fallback")
//@Retry(name = "payment-service", fallbackMethod = "fallback")
//@RateLimiter(name = "payment-service", fallbackMethod = "fallback")
//@TimeLimiter(name = "payment-service", fallbackMethod = "fallback")
@FeignClient(name = "payment-service")
public interface RestPaymentAdapter {

    @GetMapping("/v1/payment-service/payment/{shopOrderId}")
    PaymentDTO getPaymentByShopOrderId(@PathVariable(name = "shopOrderId") Long shopOrderId);
    default void fallback(Exception e) {
        throw new AppException("Payment service is down");
    }
}
