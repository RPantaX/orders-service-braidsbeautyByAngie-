package com.braidsbeautyByAngie.aggregates.dto;


import com.braidsbeautyByAngie.aggregates.types.ShopOrderHistoryStatusEnum;
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
    private ShopOrderHistoryStatusEnum status;
    private Timestamp createdAt;
}
