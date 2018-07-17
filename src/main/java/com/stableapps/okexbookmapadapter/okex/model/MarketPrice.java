package com.stableapps.okexbookmapadapter.okex.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MarketPrice {

	public String high;
	public String limitLow;
	public String vol;
	public String last;
	public String low;
	public String buy;
	@JsonProperty("hold_amount")
	public String holdAmount;
	public String sell;
	public long contractId;
	public String unitAmount;
	public String limitHigh;

}
