/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stableapps.okexbookmapadapter.okex.model.rest;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.stableapps.okexbookmapadapter.okex.model.OrderDataSpot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author aris
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceOrderResponse {

    boolean result;
    
    @JsonProperty("error_message")
    String errorMessage;
    
    @JsonProperty("error_code")
    String errorCode;
    
    @JsonProperty("order_id")
    long orderId;
    
    @JsonProperty("client_oid")
    String clientId;
    
    public String table;
    public OrderDataSpot data;
 

}
