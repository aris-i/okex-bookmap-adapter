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
public class Order {

	@JsonProperty("lever_rate")
	public double leverRate;
	public double amount;
	@JsonProperty("orderid")
	public long orderId;
	@JsonProperty("contract_id")
	public long contractId;
	public double fee;
	@JsonProperty("contract_name")
	public String contractName;
	@JsonProperty("unit_amount")
	public double unitAmount;
	@JsonProperty("price_avg")
	public double priceAvg;
	public int type;
	@JsonProperty("deal_amount")
	public double dealAmount;
	@JsonProperty("contract_type")
	public Expiration contractType;
	@JsonProperty("user_id")
	public long userId;
	@JsonProperty("system_type")
	public int systemType;
	@JsonProperty("price")
	public double price;
	@JsonProperty("create_date_str")
	public String createDateStr;
	@JsonProperty("create_date")
	public long createDate;
	public int status;
}
