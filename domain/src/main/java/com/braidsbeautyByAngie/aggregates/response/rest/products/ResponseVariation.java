package com.braidsbeautyByAngie.aggregates.response.rest.products;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ResponseVariation {
    private String variationName;
    private String options;
}
