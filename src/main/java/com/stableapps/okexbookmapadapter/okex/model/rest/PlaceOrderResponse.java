/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stableapps.okexbookmapadapter.okex.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 *
 * @author aris
 */
@Data
public class PlaceOrderResponse {

	boolean result;
	@JsonProperty("order_id")
	long orderId;
	@JsonProperty("error_code")
	String errorCode;

}
