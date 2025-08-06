package com.braidsbeautyByAngie.controller;

import com.braidsbeautyByAngie.aggregates.dto.ShopOrderHistoryDTO;
import com.braidsbeautyByAngie.ports.in.ShopOrderHistoryServiceIn;
import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.util.ApiResponse;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@OpenAPIDefinition(
        info = @Info(
                title = "API-ORDERS-HISTORY",
                version = "1.0",
                description = "Shop Order History management"
        )
)
@RestController
@RequestMapping("/v1/orders-service/order/history")
@RequiredArgsConstructor
public class OrderHistoryController {

    private final ShopOrderHistoryServiceIn shopOrderHistoryServiceIn;

    @GetMapping("/{shopOrderId}")
    public ResponseEntity<ApiResponse> findOrderHistoryByShopOrderId(@PathVariable(name = "shopOrderId") Long shopOrderId){

        return ResponseEntity.ok(ApiResponse.ok("find order history by shop Order is", shopOrderHistoryServiceIn.findByOrderIdIn(shopOrderId)));
    }

}
