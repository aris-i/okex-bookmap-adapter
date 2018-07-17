/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stableapps.okexbookmapadapter.okex.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 *
 * @author aris
 */
@Data
public class SubscribeEstimatedFuturesDeliveryPriceSecondaryResponse extends Message {

	public int binary;
	public String channel;
	public double data;
	public long timestamp;

	@JsonIgnore
	ForecastPrice forecastPrice = new ForecastPrice();

	public void setData(double data) {
		this.data = data;
		forecastPrice.setEstimatedDeliveryPrice(data);
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
		forecastPrice.setTimestamp(timestamp);
	}

}
