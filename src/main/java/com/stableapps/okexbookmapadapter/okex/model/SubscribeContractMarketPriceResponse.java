package com.stableapps.okexbookmapadapter.okex.model;

import lombok.Data;

@Data
public class SubscribeContractMarketPriceResponse extends Message {

	public int binary;
	public String channel;
	public MarketPrice data;

}
