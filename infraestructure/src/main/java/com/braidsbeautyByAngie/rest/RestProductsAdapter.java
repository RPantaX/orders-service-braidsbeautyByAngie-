package com.braidsbeautyByAngie.rest;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "products-service")
public interface RestProductsAdapter {



}
