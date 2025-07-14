package com.braidsbeautyByAngie.aggregates.constants;

import com.braidsbeautybyangie.sagapatternspringboot.aggregates.AppExceptions.TypeException;
import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.util.GenericError;

public enum OrdersErrorEnum implements GenericError {
    //Generic Errors
    //SHOP ORDER
    SHOP_ORDER_NOT_FOUND_ERSO00001("ERSO00001", "Shop Order Not Found", "The shop order with the provided ID does not exist.", TypeException.E),
    SHOP_ORDER_ALREADY_EXISTS_ERSO00002("ERSO00002", "Shop Order Already Exists", "A shop order with the same details already exists.", TypeException.E),
    SHOP_ORDER_INVALID_STATUS_ERSO00003("ERSO00003", "Invalid Shop Order Status", "The provided status for the shop order is invalid.", TypeException.E),
    SHOP_ORDER_CREATION_FAILED_ERSO00004("ERSO00004", "Shop Order Creation Failed", "Failed to create the shop order due to an internal error.", TypeException.E),
    SHOP_ORDER_UPDATE_FAILED_ERSO00005("ERSO00005", "Shop Order Update Failed", "Failed to update the shop order due to an internal error.", TypeException.E),
    SHOP_ORDER_DELETION_FAILED_ERSO00006("ERSO00006", "Shop Order Deletion Failed", "Failed to delete the shop order due to an internal error.", TypeException.E),
    SHOP_ORDER_LISTINIG_FAILED_ERSO00007("ERSO00007", "Shop Order Listing Failed", "Failed to list shop orders due to an internal error.", TypeException.E),
    SHOP_ORDER_INVALID_DATA_ERSO00008("ERSO00008", "Invalid Shop Order Data", "The provided data for the shop order is invalid.", TypeException.E),

    //SHOP ORDER HISTORY
    SHOP_ORDER_HISTORY_NOT_FOUND_ERSOH00009("ERSHO00009", "Shop Order History Not Found", "The shop order history with the provided ID does not exist.", TypeException.E),
    SHOP_ORDER_HISTORY_ALREADY_EXISTS_ERSOH00010("ERSHO00010", "Shop Order History Already Exists", "A shop order history with the same details already exists.", TypeException.E),
    SHOP_ORDER_HISTORY_INVALID_STATUS_ERSOH00011("ERSHO00011", "Invalid Shop Order History Status", "The provided status for the shop order history is invalid.", TypeException.E),
    SHOP_ORDER_HISTORY_CREATION_FAILED_ERSOH00012("ERSHO00012", "Shop Order History Creation Failed", "Failed to create the shop order history due to an internal error.", TypeException.E),
    SHOP_ORDER_HISTORY_UPDATE_FAILED_ERSOH00013("ERSHO00013", "Shop Order History Update Failed", "Failed to update the shop order history due to an internal error.", TypeException.E),
    SHOP_ORDER_HISTORY_DELETION_FAILED_ERSOH00014("ERSHO00014", "Shop Order History Deletion Failed", "Failed to delete the shop order history due to an internal error.", TypeException.E),
    SHOP_ORDER_HISTORY_LISTING_FAILED_ERSOH00015("ERSHO00015", "Shop Order History Listing Failed", "Failed to list shop order histories due to an internal error.", TypeException.E),
    SHOP_ORDER_HISTORY_INVALID_DATA_ERSOH00016("ERSHO00016", "Invalid Shop Order History Data", "The provided data for the shop order history is invalid.", TypeException.E),

    //SHOPPING METHOD
    SHOPPING_METHOD_NOT_FOUND_ERSM00017("ERSM00017", "Shopping Method Not Found", "The shopping method with the provided ID does not exist.", TypeException.E),
    //WARNING Errors
    //
    ;
    private OrdersErrorEnum(String code, String title, String message, TypeException type) {
        this.code = code;
        this.title = title;
        this.message = message;
        this.type = type;
    }
    private final String code;
    private final String title;
    private final String message;
    private final TypeException type;


    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public TypeException getType() {
        return type;
    }
}
