package com.braidsbeautyByAngie.adapters;

import com.braidsbeautyByAngie.aggregates.dto.ShopOrderDTO;
import com.braidsbeautyByAngie.aggregates.types.OrderLineStatusEnum;
import com.braidsbeautyByAngie.aggregates.types.ShopOrderStatusEnum;
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
import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.events.OrderApprovedEvent;
import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.events.OrderCreatedEvent;
import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.requests.RequestProductsEvent;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

@Service
@RequiredArgsConstructor
public class ShopOrderServiceAdapter implements ShopOrderServiceOut {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShopOrderServiceAdapter.class);

    @Value("${orders.events.topic.name}")
    private String ordersEventsTopicName;

    private final ShopOrderRepository shopOrderRepository;
    private final ShoppingMethodRepository shoppingMethodRepository;
    private final OrderLineRepository orderLineRepository;
    private final AdressRepository adressRepository;

    private final ShopOrderMapper shopOrderMapper;
    private final ShoppingMethodMapper shoppingMethodMapper;
    private final OrderLineMapper orderLineMapper;
    private final AddressMapper addressMapper;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void rejectShopOrderOut(Long orderId) {
        ShopOrderEntity shopOrderEntity = fetchShopOrderById(orderId);
        shopOrderEntity.setShopOrderStatus(ShopOrderStatusEnum.REJECTED);
        shopOrderRepository.save(shopOrderEntity);
    }

    @Override
    public void aprovedShopOrderOut(Long orderId, BigDecimal paymentTotalPrice, boolean isProduct, boolean isService) {
        ShopOrderEntity shopOrderEntity = fetchShopOrderById(orderId);
        shopOrderEntity.setShopOrderStatus(ShopOrderStatusEnum.APPROVED);
        shopOrderEntity.setShopOrderTotal(paymentTotalPrice);
        shopOrderRepository.save(shopOrderEntity);

        sendOrderApprovedEvent(shopOrderEntity, isProduct, isService);
    }

    @Transactional
    @Override
    public ShopOrderDTO createShopOrderOut(RequestShopOrder requestShopOrder) {
        ShopOrderEntity shopOrderEntity = new ShopOrderEntity();
        shopOrderEntity.setShopOrderDate(Constants.getTimestamp());
        shopOrderEntity.setCreatedAt(Constants.getTimestamp());
        shopOrderEntity.setModifiedByUser("TEST-CREATED");
        shopOrderEntity.setUserId(requestShopOrder.getUserId());
        shopOrderEntity.setShopOrderStatus(ShopOrderStatusEnum.CREATED);

        ShoppingMethodEntity shoppingMethod = fetchShoppingMethod(requestShopOrder.getShoppingMethodId());
        shopOrderEntity.setShoppingMethodEntity(shoppingMethod);

        List<OrderLineEntity> orderLines = new ArrayList<>();

        if (hasProductsAndReservation(requestShopOrder)) {
            saveAndLinkAddress(requestShopOrder, shopOrderEntity);
            orderLines.addAll(saveProducts(requestShopOrder.getProductRequestList()));
            orderLines.add(saveReservation(requestShopOrder.getReservationId()));
        } else if (hasOnlyReservation(requestShopOrder)) {
            orderLines.add(saveReservation(requestShopOrder.getReservationId()));
        } else if (hasOnlyProducts(requestShopOrder)) {
            saveAndLinkAddress(requestShopOrder, shopOrderEntity);
            orderLines.addAll(saveProducts(requestShopOrder.getProductRequestList()));
        }

        shopOrderEntity.setOrderLineEntities(orderLineRepository.saveAll(orderLines));

        ShopOrderEntity savedShopOrder = shopOrderRepository.save(shopOrderEntity);
        kafkaTemplate.send(ordersEventsTopicName, buildOrderCreatedEvent(savedShopOrder, requestShopOrder));

        return shopOrderMapper.mapShopOrderEntityToShopOrderDTO(savedShopOrder);
    }

    @Override
    public ResponseListPageableShopOrder getShopOrderListOut(int pageNumber, int pageSize, String orderBy, String sortDir) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, resolveSort(orderBy, sortDir));
        Page<ShopOrderEntity> shopOrderPage = shopOrderRepository.findAll(pageable);

        List<ResponseShopOrder> responseList = shopOrderPage.getContent().stream()
                .map(this::mapToResponseShopOrder)
                .toList();

        return ResponseListPageableShopOrder.builder()
                .responseShopOrderList(responseList)
                .pageNumber(shopOrderPage.getNumber())
                .totalElements(shopOrderPage.getTotalElements())
                .totalPages(shopOrderPage.getTotalPages())
                .pageSize(shopOrderPage.getSize())
                .end(shopOrderPage.isLast())
                .build();
    }

    // Utilidad y Métodos Privados

    private ShopOrderEntity fetchShopOrderById(Long orderId) {
        return shopOrderRepository.findById(orderId).orElseThrow(() -> new AppExceptionNotFound("Shop Order not found"));
    }

    private ShoppingMethodEntity fetchShoppingMethod(Long shoppingMethodId) {
        return shoppingMethodRepository.findById(shoppingMethodId).orElseThrow(() -> new AppExceptionNotFound("Shopping Method not found"));
    }

    private boolean hasProductsAndReservation(RequestShopOrder requestShopOrder) {
        return !requestShopOrder.getProductRequestList().isEmpty() && requestShopOrder.getReservationId() != null;
    }

    private boolean hasOnlyReservation(RequestShopOrder requestShopOrder) {
        return requestShopOrder.getProductRequestList().isEmpty() && requestShopOrder.getReservationId() != null;
    }

    private boolean hasOnlyProducts(RequestShopOrder requestShopOrder) {
        return !requestShopOrder.getProductRequestList().isEmpty() && requestShopOrder.getReservationId() == null;
    }

    private AddressEntity saveAndLinkAddress(RequestShopOrder requestShopOrder, ShopOrderEntity shopOrderEntity) {
        AddressEntity address = buildAddress(requestShopOrder);
        AddressEntity savedAddress = adressRepository.save(address);
        shopOrderEntity.setAddressEntity(savedAddress);
        return savedAddress;
    }

    private AddressEntity buildAddress(RequestShopOrder requestShopOrder) {
        return AddressEntity.builder()
                .addressCity(requestShopOrder.getRequestAdress().getAdressCity())
                .addressCountry(requestShopOrder.getRequestAdress().getAdressCountry())
                .addressStreet(requestShopOrder.getRequestAdress().getAdressStreet())
                .addressPostalCode(requestShopOrder.getRequestAdress().getAdressPostalCode())
                .addressState(requestShopOrder.getRequestAdress().getAdressState())
                .build();
    }

    private List<OrderLineEntity> saveProducts(List<ProductRequest> productRequests) {
        return productRequests.stream()
                .map(this::buildProductOrderLine)
                .toList();
    }

    private OrderLineEntity buildProductOrderLine(ProductRequest productRequest) {
        double initialPrice = 00.00;
        return OrderLineEntity.builder()
                .productItemId(productRequest.getProductId())
                .orderLineQuantity(productRequest.getProductQuantity())
                .orderLinePrice(initialPrice)
                .orderLineTotal(initialPrice)
                .orderLineState(OrderLineStatusEnum.CREATED)
                .build();
    }

    private OrderLineEntity saveReservation(Long reservationId) {
        double initialPrice = 00.00;
        int initialQuantity = 1;
        return OrderLineEntity.builder()
                .reservationId(reservationId)
                .orderLineQuantity(initialQuantity)
                .orderLinePrice(initialPrice)
                .orderLineTotal(initialPrice)
                .orderLineState(OrderLineStatusEnum.CREATED)
                .build();
    }


    private void sendOrderApprovedEvent(ShopOrderEntity shopOrderEntity, boolean isProduct, boolean isService) {
        OrderApprovedEvent event = OrderApprovedEvent.builder()
                .shopOrderId(shopOrderEntity.getShopOrderId())
                .isService(isService)
                .isProduct(isProduct)
                .build();
        kafkaTemplate.send(ordersEventsTopicName, event);
    }

    private OrderCreatedEvent buildOrderCreatedEvent(ShopOrderEntity shopOrderEntity, RequestShopOrder requestShopOrder) {
        List<RequestProductsEvent> productEvents = requestShopOrder.getProductRequestList().stream()
                .map(product -> RequestProductsEvent.builder()
                        .productId(product.getProductId())
                        .quantity(product.getProductQuantity())
                        .build())
                .toList();

        return OrderCreatedEvent.builder()
                .shopOrderId(shopOrderEntity.getShopOrderId())
                .customerId(shopOrderEntity.getUserId())
                .requestProductsEventList(productEvents)
                .reservationId(requestShopOrder.getReservationId())
                .build();
    }

    private Sort resolveSort(String orderBy, String sortDir) {
        return sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(orderBy).ascending() : Sort.by(orderBy).descending();
    }

    private ResponseShopOrder mapToResponseShopOrder(ShopOrderEntity shopOrderEntity) {
        return ResponseShopOrder.builder()
                .shopOrderDTO(shopOrderMapper.mapShopOrderEntityToShopOrderDTO(shopOrderEntity))
                .addressDTO(addressMapper.addressDTO(shopOrderEntity.getAddressEntity()))
                .orderLineDTOList(shopOrderEntity.getOrderLineEntities().stream().map(orderLineMapper::mapToDTO).toList())
                .shoppingMethodDTO(shoppingMethodMapper.convertToShopOrderDTO(shopOrderEntity.getShoppingMethodEntity()))
                .factureNumber(20L)
                .build();
    }
}
