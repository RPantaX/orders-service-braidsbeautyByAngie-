package com.braidsbeautyByAngie.aggregates.dto;


import lombok.*;

import java.sql.Timestamp;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ShopOrderHistoryDTO {
    private Long shopOrderHistoryId;
    private Long shopOrderId;
    private String status;
    private Timestamp createdAt;
}
