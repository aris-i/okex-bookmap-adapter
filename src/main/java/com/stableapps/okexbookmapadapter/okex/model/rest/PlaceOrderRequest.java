/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stableapps.okexbookmapadapter.okex.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.stableapps.okexbookmapadapter.okex.model.Expiration;
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
public class PlaceOrderRequest {

	String symbol;
	@JsonProperty("contract_type")
	Expiration expiration;
	double price;
	int amount;
	int type;
	@JsonProperty("match_price")
	int matchPrice;
	@JsonProperty("lever_rate")
	int leverRate;

}
