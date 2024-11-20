package com.braidsbeautyByAngie.saga;
import com.braidsbeautyByAngie.ports.in.ShopOrderHistoryServiceIn;
import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.commands.*;
import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.dto.PaymentType;
import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.events.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.time.LocalTime;

@Component
@KafkaListener(topics = {"${orders.events.topic.name}", "${products.events.topic.name}", "${payments.events.topic.name}", "${services.events.topic.name}"})
@RequiredArgsConstructor
public class OrderSaga {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ShopOrderHistoryServiceIn service;
    @Value("${products.commands.topic.name}")
    private String productsCommandsTopicName;

    @Value("${payments.commands.topic.name}")
    private String paymentsCommandsTopicName;

    @Value("${orders.commands.topic.name}")
    private String ordersCommandsTopicName;

    @Value("${services.commands.topic.name}")
    private String servicesCommandsTopicName;

    @KafkaHandler
    public void handleEvent(@Payload OrderCreatedEvent event){
        ReserveProductCommand command = ReserveProductCommand.builder()
                .requestProductsEventList(event.getRequestProductsEventList())
                .shopOrderId(event.getShopOrderId())
                .reservationId(event.getReservationId())
                .build();
        kafkaTemplate.send(productsCommandsTopicName, command);
        service.addIn(event.getShopOrderId(), "CREATED");
    }

//    @KafkaHandler
//    public void handleEvent(@Payload OrderCreatedToServiceEvent event){
//        ReserveServiceCommand command = ReserveServiceCommand.builder()
//                .reservationId(event.getReservationId())
//                .shopOrderId(event.getShopOrderId())
//                .build();
//        kafkaTemplate.send(servicesCommandsTopicName, command);
//        service.addIn(event.getShopOrderId(), "SERVICE-CREATED");
//    }

    @KafkaHandler
    public void handleEvent(@Payload ProductReservedEvent event){
        ReserveServiceCommand command = ReserveServiceCommand.builder()
                .reservationId(event.getReservationId())
                .shopOrderId(event.getShopOrderId())
                .productList(event.getProductList())
                .build();
        kafkaTemplate.send(servicesCommandsTopicName, command);
    }

    @KafkaHandler
    public void handleEvent(@Payload ServiceReservedEvent event){
        ProcessPaymentCommand processPaymentCommand = ProcessPaymentCommand.builder()
                .shopOrderId(event.getShopOrderId())
                .paymentProvider("PAYPAL")
                .userId(1L)
                .paymentAccountNumber(BigInteger.valueOf(123456789))
                .paymentExpirationDate(LocalTime.parse("18:11:20"))
                .paymentType(PaymentType.valueOf("CREDIT_CARD"))
                .serviceList(event.getServiceList())
                .productList(event.getProductList())
                .reservationId(event.getReservationId())
                .build();
        kafkaTemplate.send(paymentsCommandsTopicName, processPaymentCommand);
    }

    @KafkaHandler
    public void handleEvent(@Payload PaymentProcessedEvent event){
        ApproveOrderCommand approveOrderCommand = ApproveOrderCommand.builder()
                .shopOrderId(event.getShopOrderId())
                .isProduct(event.isProduct())
                .isService(event.isService())
                .build();
        kafkaTemplate.send(ordersCommandsTopicName, approveOrderCommand);
    }
    @KafkaHandler
    public void handleEvent(@Payload OrderApprovedEvent event){
        if(Boolean.TRUE.equals(event.getIsProduct()) && Boolean.TRUE.equals(event.getIsService())){
            service.addIn(event.getShopOrderId(), "APPROVED-PRODUCT");
            service.addIn(event.getShopOrderId(), "APPROVED-SERVICE");
        } else if (Boolean.TRUE.equals(event.getIsProduct())){
            service.addIn(event.getShopOrderId(), "APPROVED-PRODUCT");
        } else {
            service.addIn(event.getShopOrderId(), "APPROVED-SERVICE");
        }
    }
    @KafkaHandler
    public void handleEvent(@Payload PaymentFailedEvent event){
        CancelProductAndServiceReservationCommand cancelProductReservationCommand = CancelProductAndServiceReservationCommand.builder()
                .shopOrderId(event.getShopOrderId())
                .productList(event.getProductList())
                .reservationId(event.getReservationId())
                .build();
        kafkaTemplate.send(servicesCommandsTopicName, cancelProductReservationCommand);

    }

    @KafkaHandler
    public void handleEvent(@Payload ProductAndServiceCancelledToOrderEvent event){
        CancelProductAndServiceReservationCommand cancelProductReservationCommand = CancelProductAndServiceReservationCommand.builder()
                .shopOrderId(event.getShopOrderId())
                .productList(event.getProductList())
                .reservationId(event.getReservationId())
                .build();
        kafkaTemplate.send(productsCommandsTopicName, cancelProductReservationCommand);
    }


    @KafkaHandler
    public void handleEvent(@Payload ProductAndServiceReservationCancelledEvent event){
        RejectOrderCommand rejectOrderCommand = RejectOrderCommand.builder()
                .shopOrderId(event.getShopOrderId())
                .build();
        kafkaTemplate.send(ordersCommandsTopicName, rejectOrderCommand);
        service.addIn(event.getShopOrderId(), "REJECTED");
    }

    @KafkaHandler
    public void handleEvent(@Payload ServiceReservationFailedEvent event){
        CancelProductAndServiceReservationCommand cancelProductReservationCommand = CancelProductAndServiceReservationCommand.builder()
                .shopOrderId(event.getShopOrderId())
                .productList(event.getProductList())
                .reservationId(event.getReservationId())
                .build();
        kafkaTemplate.send(productsCommandsTopicName, cancelProductReservationCommand);
    }

    @KafkaHandler
    public void handleEvent(@Payload ProductReservationFailedEvent event){
        RejectOrderCommand rejectOrderCommand = RejectOrderCommand.builder()
                .shopOrderId(event.getShopOrderId())
                .build();
        kafkaTemplate.send(ordersCommandsTopicName, rejectOrderCommand);
        service.addIn(event.getShopOrderId(), "REJECTED");
    }
}
