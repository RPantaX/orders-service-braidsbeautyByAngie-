package com.braidsbeautyByAngie.rest;

import com.braidsbeautyByAngie.aggregates.response.rest.ApiResponse;
import com.braidsbeautyByAngie.aggregates.response.rest.products.ResponseProductItemDetail;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
//@CircuitBreaker(name = "product-service")
//@Retry(name = "product-service")
//@TimeLimiter(name = "product-service")
@FeignClient(name = "product-service")
public interface RestProductsAdapter {

    @GetMapping("/v1/product-service/itemProduct/list")
    ApiResponse<List<ResponseProductItemDetail>> listItemProductsByIds(@RequestParam("ids") List<Long> ids);

}
