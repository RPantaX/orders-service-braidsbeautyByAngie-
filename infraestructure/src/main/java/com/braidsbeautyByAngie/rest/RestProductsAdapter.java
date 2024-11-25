package com.braidsbeautyByAngie.rest;

import com.braidsbeautyByAngie.aggregates.request.rest.products.RequestProductIds;
import com.braidsbeautyByAngie.aggregates.response.rest.products.ResponseProductItemDetail;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "product-service")
public interface RestProductsAdapter {

    @GetMapping("/v1/product-service/itemProduct/list")
    List<ResponseProductItemDetail> listItemProductsByIds(@RequestParam("ids") List<Long> ids);

}
