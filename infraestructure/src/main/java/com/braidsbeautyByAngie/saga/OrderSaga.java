package com.braidsbeautyByAngie.saga;
import com.braidsbeautyByAngie.ports.in.FacturaServicIn;
import com.braidsbeautyByAngie.ports.in.ShopOrderHistoryServiceIn;
import com.braidsbeautyByAngie.ports.in.ShopOrderServiceIn;
import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.commands.ReserveProductCommand;
import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.events.OrderCreatedToProductsEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = {"${orders.events.topic.name}"})
@RequiredArgsConstructor
public class OrderSaga {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ShopOrderHistoryServiceIn service;
    @Value("${products.commands.topic.name}")
    private String productsCommandsTopicName;

    @KafkaHandler
    public void handleEvent(@Payload OrderCreatedToProductsEvent event){
        ReserveProductCommand command = ReserveProductCommand.builder()
                .productItemId(event.getProductItemId())
                .productQuantity(event.getProductQuantity())
                .shopOrderId(event.getShopOrderId())
                .build();
        kafkaTemplate.send(productsCommandsTopicName, command);
        service.addIn(event.getShopOrderId(), "CREATED");
    }
}
