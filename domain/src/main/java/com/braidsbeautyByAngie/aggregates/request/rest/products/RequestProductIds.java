package com.braidsbeautyByAngie.aggregates.request.rest.products;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class RequestProductIds {
    private List<Long> itemProductIds;
}
