package com.braidsbeautyByAngie.controller;

import com.braidsbeautyByAngie.aggregates.dto.ShopOrderDTO;
import com.braidsbeautyByAngie.aggregates.request.RequestShopOrder;
import com.braidsbeautyByAngie.aggregates.response.ResponseListPageableShopOrder;
import com.braidsbeautyByAngie.aggregates.response.ResponseShopOrderDetail;
import com.braidsbeautyByAngie.ports.in.ShopOrderServiceIn;
import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.Constants;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@OpenAPIDefinition(
        info = @Info(
                title = "API-ORDERS",
                version = "1.0",
                description = "Shop Order management"
        )
)
@RestController
@RequestMapping("/v1/orders-service/order")
@RequiredArgsConstructor
public class OrdersController {

    private final ShopOrderServiceIn service;

    @Operation(summary = "Get all orders")
    @RequestMapping("/list")
    public ResponseEntity<ResponseListPageableShopOrder> listOrders(@RequestParam(value = "pageNo", defaultValue = Constants.NUM_PAG_BY_DEFECT, required = false) int pageNo,
                                                                    @RequestParam(value = "pageSize", defaultValue = Constants.SIZE_PAG_BY_DEFECT, required = false) int pageSize,
                                                                    @RequestParam(value = "sortBy", defaultValue = Constants.ORDER_BY_DEFECT_ALL, required = false) String sortBy,
                                                                    @RequestParam(value = "sortDir", defaultValue = Constants.ORDER_DIRECT_BY_DEFECT, required = false) String sortDir){
        return ResponseEntity.ok(service.getShopOrderListIn(pageNo, pageSize, sortBy, sortDir));
    }

    @Operation(summary = "Generate order")
    @RequestMapping()
    public ResponseEntity<ShopOrderDTO> generateOrder(@RequestBody RequestShopOrder requestShopOrder){
        return new ResponseEntity<>(service.createShopOrderIn(requestShopOrder),HttpStatus.CREATED);
    }

    @GetMapping(value = "/{shopOrderId}")
    public ResponseEntity<ResponseShopOrderDetail> getShopOrderById(@PathVariable(name = "shopOrderId") Long shopOrderId){
        return ResponseEntity.ok(service.findShopOrderByIdIn(shopOrderId));
    }
}