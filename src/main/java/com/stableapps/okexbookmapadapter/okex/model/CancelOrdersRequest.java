/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stableapps.okexbookmapadapter.okex.model;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class CancelOrdersRequest {

	String symbol;
	@JsonProperty("order_id")
	String orderIds;
	@JsonProperty("contract_type")
	Expiration expiration;

}
