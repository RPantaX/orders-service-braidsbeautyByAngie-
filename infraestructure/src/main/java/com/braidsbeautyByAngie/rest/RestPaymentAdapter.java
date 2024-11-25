package com.braidsbeautyByAngie.rest;

import com.braidsbeautyByAngie.aggregates.response.rest.payments.PaymentDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "payment-service")
public interface RestPaymentAdapter {
    @GetMapping("/v1/payment-service/payment/{shopOrderId}")
    PaymentDTO getPaymentByShopOrderId(@PathVariable(name = "shopOrderId") Long shopOrderId);

}
