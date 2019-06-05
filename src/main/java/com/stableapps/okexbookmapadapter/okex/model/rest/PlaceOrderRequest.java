/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stableapps.okexbookmapadapter.okex.model.rest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.stableapps.okexbookmapadapter.okex.model.Expiration;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 *
 * @author aris
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceOrderRequest {

    @JsonProperty("client_oid")
	String clientOrderId;
    Object type;
    
//    @JsonIgnore
    @JsonInclude(Include.NON_NULL)
    @JsonProperty("size")
    Double floatingPointSize;
    
    @JsonInclude(Include.NON_NULL)
    @JsonProperty("order_id")
    String orderId;

}
