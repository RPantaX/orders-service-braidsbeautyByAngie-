package com.braidsbeautyByAngie.adapters.handler;

import com.braidsbeautyByAngie.ports.in.ShopOrderServiceIn;
import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.commands.ApproveOrderCommand;
import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.commands.RejectOrderCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = {"${orders.commands.topic.name}"})
@RequiredArgsConstructor
public class OrderCommandsHandler {

    private final ShopOrderServiceIn service;

    @KafkaHandler
    public void handleCommand(@Payload ApproveOrderCommand command){
        service.aprovedShopOrderIn(command.getShopOrderId(), command.getIsProduct(), command.getIsService());
    }
    @KafkaHandler
    public void handleCommand(@Payload RejectOrderCommand command){
        service.rejectShopOrderIn(command.getShopOrderId());
    }

}
