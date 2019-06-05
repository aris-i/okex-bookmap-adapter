package com.stableapps.okexbookmapadapter.okex.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderData {

    @JsonProperty("order_id")
    String orderId;
    
    @JsonProperty("client_oid")
    String clientOid;
    
    double price;
    double size;
    
    @JsonProperty("instrument_id")
    String instrumentId;
    
    String type;
    String timestamp;
    
    @JsonProperty("order_type")
    int orderType;
    
    @JsonProperty("last_fill_px")
    double lastFillPx;

    @JsonProperty("last_fill_qty")
    double lastFilledQty;
    
    @JsonProperty("last_fill_time")
    String lastFillTime;
    
    Integer state;

    @JsonIgnore
    String instrumentType;
    
    
}
