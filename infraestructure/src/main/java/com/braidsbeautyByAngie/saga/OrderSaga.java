package com.braidsbeautyByAngie.saga;
import com.braidsbeautyByAngie.aggregates.types.ShopOrderHistoryStatusEnum;
import com.braidsbeautyByAngie.ports.in.ShopOrderHistoryServiceIn;
import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.commands.*;
import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.dto.PaymentType;
import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.events.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
        log.info("Received OrderCreatedEvent: {}", event);
        ReserveProductCommand command = ReserveProductCommand.builder()
                .requestProductsEventList(event.getRequestProductsEventList())
                .shopOrderId(event.getShopOrderId())
                .reservationId(event.getReservationId())
                .build();
        try {
            kafkaTemplate.send(productsCommandsTopicName, command);
            log.info("Sent ReserveProductCommand to topic {}: {}", productsCommandsTopicName, command);
        } catch (Exception e) {
            log.error("Failed to send ReserveProductCommand: {}", command, e);
            throw e; // Deja que el error se maneje en niveles superiores
        }

        service.addIn(event.getShopOrderId(), ShopOrderHistoryStatusEnum.CREATED);
        log.info("Order history updated with status: {}", ShopOrderHistoryStatusEnum.CREATED);
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
        log.info("Received ProductReservedEvent: {}", event);
        if(event.getReservationId() == null || event.getReservationId() == 0) {
            log.info("Received ServiceReservedEvent: {}", event);
            try {
                ProcessPaymentCommand processPaymentCommand = ProcessPaymentCommand.builder()
                        .shopOrderId(event.getShopOrderId())
                        .paymentProvider("PAYPAL")
                        .userId(1L)
                        .paymentAccountNumber(BigInteger.valueOf(123456789))
                        .paymentExpirationDate(LocalTime.parse("18:11:20"))
                        .paymentType(PaymentType.valueOf("CREDIT_CARD"))
                        .productList(event.getProductList())
                        .build();
                kafkaTemplate.send(paymentsCommandsTopicName, processPaymentCommand);
                log.info("Sent ProcessPaymentCommand to topic {}: {}", paymentsCommandsTopicName, processPaymentCommand);
            } catch (Exception e) {
                log.error("Error handling ServiceReservedEvent: {}", event, e);
            }
        } else{
            try {
                ReserveServiceCommand command = ReserveServiceCommand.builder()
                        .reservationId(event.getReservationId())
                        .shopOrderId(event.getShopOrderId())
                        .productList(event.getProductList())
                        .build();
                kafkaTemplate.send(servicesCommandsTopicName, command);
                log.info("Sent ReserveServiceCommand to topic {}: {}", servicesCommandsTopicName, command);
            } catch (Exception e) {
                log.error("Error handling ProductReservedEvent: {}", event, e);
            }
        }

    }

    @KafkaHandler
    public void handleEvent(@Payload ServiceReservedEvent event){
        log.info("Received ServiceReservedEvent: {}", event);
        try {
            ProcessPaymentCommand processPaymentCommand = ProcessPaymentCommand.builder()
                    .shopOrderId(event.getShopOrderId())
                    .paymentProvider("PAYPAL")
                    .userId(1L)
                    .paymentAccountNumber(BigInteger.valueOf(123456789))
                    .paymentExpirationDate(LocalTime.parse("18:11:20"))
                    .paymentType(PaymentType.valueOf("CREDIT_CARD"))
                    .productList(event.getProductList())
                    .reservationCore(event.getReservationCore())
                    .build();
            kafkaTemplate.send(paymentsCommandsTopicName, processPaymentCommand);
            log.info("Sent ProcessPaymentCommand to topic {}: {}", paymentsCommandsTopicName, processPaymentCommand);
        } catch (Exception e) {
            log.error("Error handling ServiceReservedEvent: {}", event, e);
        }
    }

    @KafkaHandler
    public void handleEvent(@Payload PaymentProcessedEvent event){
        log.info("Received PaymentProcessedEvent: {}", event);
        try {
            ApproveOrderCommand approveOrderCommand = ApproveOrderCommand.builder()
                    .shopOrderId(event.getShopOrderId())
                    .paymentTotalPrice(event.getPaymentTotalPrice())
                    .isProduct(event.isProduct())
                    .isService(event.isService())
                    .build();
            kafkaTemplate.send(ordersCommandsTopicName, approveOrderCommand);
            log.info("Sent ApproveOrderCommand to topic {}: {}", ordersCommandsTopicName, approveOrderCommand);
        } catch (Exception e) {
            log.error("Error handling PaymentProcessedEvent: {}", event, e);
        }
    }
    @KafkaHandler
    public void handleEvent(@Payload OrderApprovedEvent event){
        log.info("Received OrderApprovedEvent: {}", event);
        try {
            if (Boolean.TRUE.equals(event.getIsProduct()) && Boolean.TRUE.equals(event.getIsService())) {
                service.addIn(event.getShopOrderId(), ShopOrderHistoryStatusEnum.APPROVED_PRODUCT);
                service.addIn(event.getShopOrderId(), ShopOrderHistoryStatusEnum.APPROVED_SERVICE);
                log.info("Order approved for both product and service.");
            } else if (Boolean.TRUE.equals(event.getIsProduct())) {
                service.addIn(event.getShopOrderId(), ShopOrderHistoryStatusEnum.APPROVED_PRODUCT);
                log.info("Order approved for product only.");
            } else {
                service.addIn(event.getShopOrderId(), ShopOrderHistoryStatusEnum.APPROVED_SERVICE);
                log.info("Order approved for service only.");
            }
        } catch (Exception e) {
            log.error("Error handling OrderApprovedEvent: {}", event, e);
        }
    }
    @KafkaHandler
    public void handleEvent(@Payload PaymentFailedEvent event){
        log.info("Received PaymentFailedEvent: {}", event);
        try {
            CancelProductAndServiceReservationCommand cancelProductReservationCommand = CancelProductAndServiceReservationCommand.builder()
                    .shopOrderId(event.getShopOrderId())
                    .productList(event.getProductList())
                    .reservationId(event.getReservationId())
                    .build();
            kafkaTemplate.send(servicesCommandsTopicName, cancelProductReservationCommand);
        } catch (Exception e) {
            log.error("Error handling PaymentFailedEvent: {}", event, e);
        }
    }

    @KafkaHandler
    public void handleEvent(@Payload ProductAndServiceCancelledToOrderEvent event){
        log.info("Received ProductAndServiceCancelledToOrderEvent: {}", event);
        try{
            CancelProductAndServiceReservationCommand cancelProductReservationCommand = CancelProductAndServiceReservationCommand.builder()
                    .shopOrderId(event.getShopOrderId())
                    .productList(event.getProductList())
                    .reservationId(event.getReservationId())
                    .build();
            kafkaTemplate.send(productsCommandsTopicName, cancelProductReservationCommand);
            log.info("Sent CancelProductAndServiceReservationCommand to topic {}: {}", productsCommandsTopicName, cancelProductReservationCommand);
        } catch (Exception e) {
            log.error("Error handling ProductAndServiceCancelledToOrderEvent: {}", event, e);
        }
    }

    @KafkaHandler
    public void handleEvent(@Payload ProductAndServiceReservationCancelledEvent event){
        log.info("Received ProductAndServiceReservationCancelledEvent: {}", event);
        try {
            RejectOrderCommand rejectOrderCommand = RejectOrderCommand.builder()
                    .shopOrderId(event.getShopOrderId())
                    .build();
            kafkaTemplate.send(ordersCommandsTopicName, rejectOrderCommand);
            service.addIn(event.getShopOrderId(), ShopOrderHistoryStatusEnum.REJECTED);
            log.info("Sent RejectOrderCommand to topic {}: {}", ordersCommandsTopicName, rejectOrderCommand);
        } catch (Exception e) {
            log.error("Error handling ProductAndServiceReservationCancelledEvent: {}", event, e);
        }
    }

    @KafkaHandler
    public void handleEvent(@Payload ServiceReservationFailedEvent event){
        log.info("Received ServiceReservationFailedEvent: {}", event);
        try {
            CancelProductAndServiceReservationCommand cancelProductReservationCommand = CancelProductAndServiceReservationCommand.builder()
                    .shopOrderId(event.getShopOrderId())
                    .productList(event.getProductList())
                    .reservationId(event.getReservationId())
                    .build();
            kafkaTemplate.send(productsCommandsTopicName, cancelProductReservationCommand);
            log.info("Sent CancelProductAndServiceReservationCommand to topic {}: {}", productsCommandsTopicName, cancelProductReservationCommand);
        } catch (Exception e) {
            log.error("Error handling ServiceReservationFailedEvent: {}", event, e);
        }

    }

    @KafkaHandler
    public void handleEvent(@Payload ProductReservationFailedEvent event){
        log.info("Received ProductReservationFailedEvent: {}", event);
        try {
            RejectOrderCommand rejectOrderCommand = RejectOrderCommand.builder()
                    .shopOrderId(event.getShopOrderId())
                    .build();
            kafkaTemplate.send(ordersCommandsTopicName, rejectOrderCommand);
            service.addIn(event.getShopOrderId(), ShopOrderHistoryStatusEnum.REJECTED);
            log.info("Sent RejectOrderCommand to topic {}: {}", ordersCommandsTopicName, rejectOrderCommand);
        } catch (Exception e) {
            log.error("Error handling ProductReservationFailedEvent: {}", event, e);
        }

    }
}
