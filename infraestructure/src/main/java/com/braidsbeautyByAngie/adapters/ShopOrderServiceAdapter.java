package com.braidsbeautyByAngie.adapters;

import com.braidsbeautyByAngie.aggregates.dto.ShopOrderDTO;
import com.braidsbeautyByAngie.ports.out.ShopOrderServiceOut;

import com.braidsbeautyByAngie.aggregates.request.ProductRequest;
import com.braidsbeautyByAngie.aggregates.request.RequestShopOrder;
import com.braidsbeautyByAngie.aggregates.response.ResponseListPageableShopOrder;
import com.braidsbeautyByAngie.aggregates.response.ResponseShopOrder;

import com.braidsbeautyByAngie.entity.AddressEntity;
import com.braidsbeautyByAngie.entity.OrderLineEntity;
import com.braidsbeautyByAngie.entity.ShopOrderEntity;
import com.braidsbeautyByAngie.entity.ShoppingMethodEntity;

import com.braidsbeautyByAngie.mapper.AddressMapper;
import com.braidsbeautyByAngie.mapper.OrderLineMapper;
import com.braidsbeautyByAngie.mapper.ShopOrderMapper;
import com.braidsbeautyByAngie.mapper.ShoppingMethodMapper;

import com.braidsbeautyByAngie.repository.AdressRepository;
import com.braidsbeautyByAngie.repository.OrderLineRepository;
import com.braidsbeautyByAngie.repository.ShopOrderRepository;
import com.braidsbeautyByAngie.repository.ShoppingMethodRepository;


import com.braidsbeautybyangie.sagapatternspringboot.aggregates.AppExceptions.AppExceptionNotFound;
import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class ShopOrderServiceAdapter implements ShopOrderServiceOut {

    private final ShopOrderRepository shopOrderRepository;
    private final ShoppingMethodRepository shoppingMethodRepository;
    private final OrderLineRepository orderLineRepository;
    private final AdressRepository adressRepository;

    private final ShopOrderMapper shopOrderMapper;
    private final ShoppingMethodMapper shoppingMethodMapper;
    private final OrderLineMapper orderLineMapper;
    private final AddressMapper addressMapper;

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final Logger LOGGER = LoggerFactory.getLogger(ShopOrderServiceAdapter.class);
    @Value("${orders.events.topic.name}") String ordersEventsTopicName;

    @Override
    public void rejectShopOrderOut(Long orderId) {
        ShopOrderEntity shopOrderEntity = shopOrderRepository.findById(orderId).orElseThrow(
                () -> new AppExceptionNotFound("Shop Order not found")
        );
        shopOrderEntity.setShopOrderStatus("REJECTED");
        shopOrderRepository.save(shopOrderEntity);
        //TODO: send to kafka
    }

    @Override
    public void aprovedShopOrderOut(Long orderId) {
        ShopOrderEntity shopOrderEntity = shopOrderRepository.findById(orderId).orElseThrow(
                () -> new AppExceptionNotFound("Shop Order not found")
        );
        shopOrderEntity.setShopOrderStatus("APROVED");
        shopOrderRepository.save(shopOrderEntity);
        //TODO: send to kafka
    }
    @Transactional
    @Override
    public ShopOrderDTO createShopOrderOut(RequestShopOrder requestShopOrder) {
        ShopOrderEntity shopOrderEntity = new ShopOrderEntity();
        ShoppingMethodEntity shoppingMethodEntity = shoppingMethodRepository.findById(requestShopOrder.getShoppingMethodId()).orElseThrow(
                () -> new AppExceptionNotFound("Shopping Method not found")
        );
        double totalShopOrder;
        if(!requestShopOrder.getProductRequestList().isEmpty() && requestShopOrder.getReservationId() != null){
            //products and services
            //ADDRESS
            AddressEntity addressEntity = saveAddressEntity(requestShopOrder);
            AddressEntity addressEntitySaved = adressRepository.save(addressEntity);
            //PRODUCTS
            List<OrderLineEntity> orderList = new ArrayList<>(List.of());
            orderList.addAll(saveProducts(requestShopOrder.getProductRequestList()));
            //RESERVATION
            OrderLineEntity orderWithReservation = saveReservation(requestShopOrder.getReservationId());
            //ADD TO ORDER
            orderList.add(orderWithReservation);
            //save to order
            List<OrderLineEntity> orderListSaved = orderLineRepository.saveAll(orderList);
            //TOTAL
            totalShopOrder = orderList.stream().mapToDouble(OrderLineEntity::getOrderLineTotal).sum();
            //SAVE TO shopORDER
            shopOrderEntity.setAddressEntity(addressEntitySaved);
            shopOrderEntity.setOrderLineEntities(orderListSaved);
            shopOrderEntity.setShopOrderTotal(totalShopOrder);
        }
        else if ( requestShopOrder.getProductRequestList().isEmpty()){
            //services only

            List<OrderLineEntity> orderList = new ArrayList<>(List.of());

            OrderLineEntity orderWithReservation = saveReservation(requestShopOrder.getReservationId());
            orderList.add(orderWithReservation);
            //save to order
            List<OrderLineEntity> orderListSaved = orderLineRepository.saveAll(orderList);
            totalShopOrder = orderWithReservation.getOrderLineTotal();

            //SAVE TO shopORDER
            shopOrderEntity.setOrderLineEntities(orderListSaved);
            shopOrderEntity.setShopOrderTotal(totalShopOrder);
        } else{
            //products only

            //ADDRESS
            AddressEntity addressEntity = saveAddressEntity(requestShopOrder);
            AddressEntity addressEntitySaved = adressRepository.save(addressEntity);

            //PRODUCTS
            List<OrderLineEntity> orderList = saveProducts(requestShopOrder.getProductRequestList());
            totalShopOrder = orderList.stream().mapToDouble(OrderLineEntity::getOrderLineTotal).sum();
            //save to order
            List<OrderLineEntity> orderListSaved = orderLineRepository.saveAll(orderList);
            //SAVE TO shopORDER
            shopOrderEntity.setAddressEntity(addressEntitySaved);
            shopOrderEntity.setOrderLineEntities(orderListSaved);
            shopOrderEntity.setShopOrderTotal(totalShopOrder);
        }
        shopOrderEntity.setShopOrderDate(Constants.getTimestamp());
        shopOrderEntity.setShopOrderStatus("CREATED");
        shopOrderEntity.setShoppingMethodEntity(shoppingMethodEntity);
        shopOrderEntity.setCreatedAt(Constants.getTimestamp());
        shopOrderEntity.setModifiedByUser("TEST-CREATED");
        shopOrderEntity.setUserId(requestShopOrder.getUserId());

        ShopOrderEntity shopOrderEntitySaved = shopOrderRepository.save(shopOrderEntity);

        //TODO: send to kafka
        kafkaTemplate.send(ordersEventsTopicName, shopOrderEntitySaved);
        return shopOrderMapper.mapShopOrderEntityToShopOrderDTO(shopOrderEntitySaved);
    }

    @Override
    public ResponseListPageableShopOrder getShopOrderListOut(int pageNumber, int pageSize, String orderBy, String sortDir) {
        LOGGER.info("Searching all shop orders with the following parameters: {}", Constants.parametersForLogger(pageNumber, pageSize, orderBy, sortDir));
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(orderBy).ascending() : Sort.by(orderBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<ShopOrderEntity> shopOrderEntityPage = shopOrderRepository.findAll(pageable);

        List<ResponseShopOrder> responseShopOrderList = shopOrderEntityPage.getContent().stream().map(
                shopOrderEntity -> ResponseShopOrder.builder()
                        .shopOrderDTO(shopOrderMapper.mapShopOrderEntityToShopOrderDTO(shopOrderEntity))
                        .addressDTO(addressMapper.addressDTO(shopOrderEntity.getAddressEntity()))
                        .orderLineDTOList(shopOrderEntity.getOrderLineEntities().stream().map(
                                orderLineMapper::mapToDTO
                        ).toList())
                        .shoppingMethodDTO(shoppingMethodMapper.convertToShopOrderDTO(shopOrderEntity.getShoppingMethodEntity()))
                        .factureNumber(20L)
                        .build()
        ).toList();

        return ResponseListPageableShopOrder.builder()
                .responseShopOrderList(responseShopOrderList)
                .pageNumber(shopOrderEntityPage.getNumber())
                .totalElements(shopOrderEntityPage.getTotalElements())
                .totalPages(shopOrderEntityPage.getTotalPages())
                .pageSize(shopOrderEntityPage.getSize())
                .end(shopOrderEntityPage.isLast())
                .build();
    }

    private AddressEntity saveAddressEntity( RequestShopOrder requestShopOrder){
        return AddressEntity.builder()
                .addressCity(requestShopOrder.getRequestAdress().getAdressCity())
                .addressCountry(requestShopOrder.getRequestAdress().getAdressCountry())
                .addressStreet(requestShopOrder.getRequestAdress().getAdressStreet())
                .addressPostalCode(requestShopOrder.getRequestAdress().getAdressPostalCode())
                .addressState(requestShopOrder.getRequestAdress().getAdressState())
                .build();
    }
    private List<OrderLineEntity> saveProducts(List<ProductRequest> productRequestList){
        return productRequestList.stream().map(
                productRequest -> OrderLineEntity.builder()
                        .productItemId(productRequest.getProductId())
                        .orderLineQuantity(productRequest.getProductQuantity())
                        .orderLinePrice(10.00)
                        .orderLineTotal(10.00 * productRequest.getProductQuantity())
                        .orderLineState("CREATED")
                        .build()
        ).toList();
    }
    private OrderLineEntity saveReservation(Long reservationId){
        return OrderLineEntity.builder()
                .reservationId(reservationId)
                .orderLineQuantity(1)
                .orderLinePrice(20.00)
                .orderLineTotal(20.00)
                .orderLineState("CREATED")
                .build();
    }
}
