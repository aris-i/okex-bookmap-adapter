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
public class EstimatedFuturesDeliveryPriceResponse extends Message {

	private double data;
	private long timestamp;

	@JsonIgnore
	ForecastPrice forecastPrice = new ForecastPrice();

	/**
	 * @param data the data to set
	 */
	public void setData(double data) {
		this.data = data;
		forecastPrice.setEstimatedDeliveryPrice(data);
	}

	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
		forecastPrice.setTimestamp(timestamp);
	}
	
}
