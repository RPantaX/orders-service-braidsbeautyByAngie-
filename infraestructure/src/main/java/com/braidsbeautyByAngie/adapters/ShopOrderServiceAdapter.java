package com.braidsbeautyByAngie.adapters;

import com.braidsbeautyByAngie.aggregates.constants.OrdersErrorEnum;
import com.braidsbeautyByAngie.aggregates.dto.AddressDTO;
import com.braidsbeautyByAngie.aggregates.dto.OrderLineDTO;
import com.braidsbeautyByAngie.aggregates.dto.ShopOrderDTO;
import com.braidsbeautyByAngie.aggregates.dto.ShoppingMethodDTO;
import com.braidsbeautyByAngie.aggregates.response.ResponseShopOrderDetail;
import com.braidsbeautyByAngie.aggregates.response.rest.payments.PaymentDTO;
import com.braidsbeautyByAngie.aggregates.response.rest.products.ResponseProductItemDetail;
import com.braidsbeautyByAngie.aggregates.response.rest.reservations.ResponseReservationDetail;
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


import com.braidsbeautyByAngie.rest.RestPaymentAdapter;
import com.braidsbeautyByAngie.rest.RestProductsAdapter;
import com.braidsbeautyByAngie.rest.RestServicesAdapter;
import com.braidsbeautybyangie.sagapatternspringboot.aggregates.AppExceptions.AppExceptionNotFound;
import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.Constants;
import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.events.OrderApprovedEvent;
import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.events.OrderCreatedEvent;
import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.requests.RequestProductsEvent;

import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.util.ValidateUtil;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
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
@Slf4j
public class ShopOrderServiceAdapter implements ShopOrderServiceOut {

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

    private final RestProductsAdapter restProductsAdapter;
    private final RestServicesAdapter restServicesAdapter;
    private final RestPaymentAdapter restPaymentAdapter;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void rejectShopOrderOut(Long orderId) {
        ShopOrderEntity shopOrderEntity = fetchShopOrderById(orderId);
        shopOrderEntity.setShopOrderStatus(ShopOrderStatusEnum.REJECTED);
        shopOrderRepository.save(shopOrderEntity);
        log.info("Shop Order Rejected: {}", shopOrderEntity);
    }

    @Override
    public void aprovedShopOrderOut(Long orderId, BigDecimal paymentTotalPrice, boolean isProduct, boolean isService) {
        ShopOrderEntity shopOrderEntity = fetchShopOrderById(orderId);
        shopOrderEntity.setShopOrderStatus(ShopOrderStatusEnum.APPROVED);
        shopOrderEntity.setShopOrderTotal(paymentTotalPrice);
        shopOrderRepository.save(shopOrderEntity);
        sendOrderApprovedEvent(shopOrderEntity, isProduct, isService);
        log.info("Shop Order Approved: {}", shopOrderEntity);
    }

    @Transactional
    @Override
    public ShopOrderDTO createShopOrderOut(RequestShopOrder requestShopOrder) {
        log.info("Creating Shop Order: {}", requestShopOrder);
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
            AddressEntity addressSaved =  saveAndLinkAddress(requestShopOrder);
            shopOrderEntity.setAddressEntity(addressSaved);
            orderLines.addAll(saveProducts(requestShopOrder.getProductRequestList()));
            orderLines.add(saveReservation(requestShopOrder.getReservationId()));
            log.info("Shop Order with Products and Reservation: {}", requestShopOrder);
        } else if (hasOnlyReservation(requestShopOrder)) {
            orderLines.add(saveReservation(requestShopOrder.getReservationId()));
            log.info("Shop Order with Reservation only: {}", requestShopOrder);
        } else if (hasOnlyProducts(requestShopOrder)) {
            AddressEntity addressSaved =  saveAndLinkAddress(requestShopOrder);
            shopOrderEntity.setAddressEntity(addressSaved);
            orderLines.addAll(saveProducts(requestShopOrder.getProductRequestList()));
            log.info("Shop Order with Products only: {}", requestShopOrder);
        }
        ShopOrderEntity savedShopOrder = shopOrderRepository.save(shopOrderEntity);
        log.info("Shop Order saved: {}", savedShopOrder);

        orderLines.forEach(orderLine -> orderLine.setShopOrderEntity(savedShopOrder));
        try {
            orderLineRepository.saveAll(orderLines);
            log.info("Order Lines saved: {}", orderLines);

            kafkaTemplate.send(ordersEventsTopicName, buildOrderCreatedEvent(savedShopOrder, requestShopOrder));
            log.info("Order Created Event sent: {}", savedShopOrder);
        } catch (Exception e) {
            log.error("Error saving Order Lines or sending Order Created Event: {}", e.getMessage());
        }

        return shopOrderMapper.mapShopOrderEntityToShopOrderDTO(savedShopOrder);
    }

    @Override
    public ResponseListPageableShopOrder getShopOrderListOut(int pageNumber, int pageSize, String orderBy, String sortDir) {
        log.info("Fetching Shop Order List");
        Pageable pageable = PageRequest.of(pageNumber, pageSize, resolveSort(orderBy, sortDir));
        Page<ShopOrderEntity> shopOrderPage = shopOrderRepository.findAll(pageable);

        List<ResponseShopOrder> responseList = shopOrderPage.getContent().stream()
                .map(this::mapToResponseShopOrder)
                .toList();
        if (responseList.isEmpty()) {
            log.info("Shop Order List is Empty");
        }
        return ResponseListPageableShopOrder.builder()
                .responseShopOrderList(responseList)
                .pageNumber(shopOrderPage.getNumber())
                .totalElements(shopOrderPage.getTotalElements())
                .totalPages(shopOrderPage.getTotalPages())
                .pageSize(shopOrderPage.getSize())
                .end(shopOrderPage.isLast())
                .build();
    }

    @Override
    public ResponseShopOrderDetail findShopOrderByIdOut(Long orderId) {
        log.info("Fetching Shop Order by Id: {}", orderId);
        ShopOrderEntity shopOrderEntity = fetchShopOrderById(orderId);
        ShopOrderDTO shopOrderDTO = shopOrderMapper.mapShopOrderEntityToShopOrderDTO(shopOrderEntity);
        ShoppingMethodDTO shoppingMethodDTO = shoppingMethodMapper.convertToShopOrderDTO(shopOrderEntity.getShoppingMethodEntity());
        List<OrderLineDTO> orderLineDTOList = shopOrderEntity.getOrderLineEntities().stream().map(orderLineMapper::mapToDTO).toList();
        // Manejar el llamado al servicio de pagos con resiliencia
        //TODO: CAPTURAR EL ERROR Y ENVIAR UN DTO POR DEFECTO
        PaymentDTO paymentDTO = new PaymentDTO();
        try{
            log.info("Fetching Payment by Shop Order Id: {}", shopOrderEntity.getShopOrderId());
            paymentDTO = restPaymentAdapter.getPaymentByShopOrderId(shopOrderEntity.getShopOrderId()).getData();
            log.info("Payment fetched successfully: {}", paymentDTO);
        } catch (Exception e){
            log.error("Error fetching Payment by Shop Order Id: {}", e.getMessage());
        }

        AddressDTO addressDTO = addressMapper.addressDTO(shopOrderEntity.getAddressEntity());
        ResponseShopOrderDetail responseShopOrderDetail = ResponseShopOrderDetail.builder()
                .shopOrderId(shopOrderDTO.getShopOrderId())
                .shopOrderStatus(shopOrderDTO.getShopOrderStatus())
                .shippingMethod(shoppingMethodDTO.getShoppingMethodName())
                .sopOrderDate(shopOrderDTO.getShopOrderDate())
                .addressDTO(addressDTO)
                .paymentDTO(paymentDTO)
                .orderLineDTOList(orderLineDTOList)
                .build();

        List<Long> itemProductIds = orderLineDTOList.stream().map(OrderLineDTO::getProductItemId).toList();
        if(!itemProductIds.isEmpty()){
            log.info("Fetching Products by Ids: {}", itemProductIds);
            try {
                log.info("Fetching Products by Ids: {}", itemProductIds);
                List<ResponseProductItemDetail> responseProductItemDetailList = restProductsAdapter.listItemProductsByIds(itemProductIds).getData();
                responseShopOrderDetail.setResponseProductItemDetailList(responseProductItemDetailList);
                log.info("Products fetched successfully: {}", responseProductItemDetailList);
            } catch (Exception e){
                log.error("Error fetching Products by Ids: {}", e.getMessage());
            }

        }
        Long reservationId = orderLineDTOList.stream().filter(orderLineDTO -> orderLineDTO.getReservationId() != null).findFirst().map(OrderLineDTO::getReservationId).orElse(null);
        if(reservationId != null && reservationId > 0) {
            try {
                log.info("Fetching Reservation by Id: {}", reservationId);
                ResponseReservationDetail responseReservationDetail = restServicesAdapter.listReservationById(reservationId).getData();
                responseShopOrderDetail.setResponseReservationDetail(responseReservationDetail);
                log.info("Reservation fetched successfully: {}", responseReservationDetail);
            } catch (Exception e){
                log.error("Error fetching Reservation by Id: {}", e.getMessage());
            }

        }
        return responseShopOrderDetail;
    }

    private ShopOrderEntity fetchShopOrderById(Long orderId) {
        log.info("Fetching Shop Order by Id: {}", orderId);
        ShopOrderEntity shopOrderEntity = shopOrderRepository.findById(orderId).orElse(null);
        if (shopOrderEntity == null) {
            log.error("Shop Order not found with Id: {}", orderId);
            ValidateUtil.requerido(shopOrderEntity, OrdersErrorEnum.SHOP_ORDER_NOT_FOUND_ERSO00001);
        }
        return shopOrderEntity;
    }

    private ShoppingMethodEntity fetchShoppingMethod(Long shoppingMethodId) {
        log.info("Fetching Shopping Method by Id: {}", shoppingMethodId);
        ShoppingMethodEntity shoppingMethod = shoppingMethodRepository.findById(shoppingMethodId).orElse(null);
        if(shoppingMethod == null) {
            log.error("Shopping Method not found with Id: {}", shoppingMethodId);
            ValidateUtil.requerido(shoppingMethod, OrdersErrorEnum.SHOPPING_METHOD_NOT_FOUND_ERSM00017);
        }
        return shoppingMethod;
    }

    private boolean hasProductsAndReservation(RequestShopOrder requestShopOrder) {
        return !requestShopOrder.getProductRequestList().isEmpty() && requestShopOrder.getReservationId() != null && requestShopOrder.getReservationId() > 0;
    }

    private boolean hasOnlyReservation(RequestShopOrder requestShopOrder) {
        return requestShopOrder.getProductRequestList().isEmpty() && requestShopOrder.getReservationId() != null && requestShopOrder.getReservationId() > 0;
    }

    private boolean hasOnlyProducts(RequestShopOrder requestShopOrder) {
        return !requestShopOrder.getProductRequestList().isEmpty() && (requestShopOrder.getReservationId() == null || requestShopOrder.getReservationId() <= 0);
    }

    private AddressEntity saveAndLinkAddress(RequestShopOrder requestShopOrder) {
        AddressEntity address = buildAddress(requestShopOrder);
        return adressRepository.save(address);
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
        log.info("Sending Order Approved Event: {}", event);
        try{
            kafkaTemplate.send(ordersEventsTopicName, event);
        }catch (Exception e){
            log.error("Error sending Order Approved Event: {}", e.getMessage());
        }
    }

    private OrderCreatedEvent buildOrderCreatedEvent(ShopOrderEntity shopOrderEntity, RequestShopOrder requestShopOrder) {
        List<RequestProductsEvent> productEvents = requestShopOrder.getProductRequestList().stream()
                .map(product -> RequestProductsEvent.builder()
                        .productId(product.getProductId())
                        .quantity(product.getProductQuantity())
                        .build())
                .toList();
        Long reservationId = null;
        if (requestShopOrder.getReservationId() != null && requestShopOrder.getReservationId() > 0) {
            reservationId = requestShopOrder.getReservationId();
        }
        return OrderCreatedEvent.builder()
                .shopOrderId(shopOrderEntity.getShopOrderId())
                .customerId(shopOrderEntity.getUserId())
                .requestProductsEventList(productEvents)
                .reservationId(reservationId)
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
