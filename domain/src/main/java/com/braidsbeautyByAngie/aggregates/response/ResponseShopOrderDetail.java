package com.braidsbeautyByAngie.aggregates.response;

import com.braidsbeautyByAngie.aggregates.dto.AddressDTO;
import com.braidsbeautyByAngie.aggregates.dto.OrderLineDTO;
import com.braidsbeautyByAngie.aggregates.response.rest.payments.PaymentDTO;
import com.braidsbeautyByAngie.aggregates.response.rest.products.ResponseProductItemDetail;
import com.braidsbeautyByAngie.aggregates.response.rest.reservations.ResponseReservationDetail;
import com.braidsbeautyByAngie.aggregates.types.ShopOrderStatusEnum;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ResponseShopOrderDetail {
    private Long shopOrderId;
    private ShopOrderStatusEnum shopOrderStatus;
    private Timestamp sopOrderDate;
    private String shippingMethod;
    private AddressDTO addressDTO;
    private PaymentDTO paymentDTO;
    private List<OrderLineDTO> orderLineDTOList;
    private List<ResponseProductItemDetail> responseProductItemDetailList;
    private ResponseReservationDetail responseReservationDetail;
}
