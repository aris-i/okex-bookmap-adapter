package com.stableapps.okexbookmapadapter.okex.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contract {

	@JsonProperty("contract_type")
	public String contractType;
	@JsonProperty("freeze")
	public Double freeze;
	@JsonProperty("balance")
	public Double balance;
	@JsonProperty("contract_id")
	public Long contractId;
	@JsonProperty("available")
	public Double available;
	@JsonProperty("profit")
	public Double profit;
	@JsonProperty("bond")
	public Double bond;
	@JsonProperty("unprofit")
	public Double unprofit;

}
