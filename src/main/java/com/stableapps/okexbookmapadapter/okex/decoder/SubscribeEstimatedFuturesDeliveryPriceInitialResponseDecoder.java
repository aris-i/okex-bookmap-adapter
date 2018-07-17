/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stableapps.okexbookmapadapter.okex.decoder;

import com.stableapps.okexbookmapadapter.okex.model.SubscribeEstimatedFuturesDeliveryPriceInitialResponse;

/**
 *
 * @author aris
 */
public class SubscribeEstimatedFuturesDeliveryPriceInitialResponseDecoder
extends AbstractDecoder<SubscribeEstimatedFuturesDeliveryPriceInitialResponse>{

	public SubscribeEstimatedFuturesDeliveryPriceInitialResponseDecoder() {
		super(SubscribeEstimatedFuturesDeliveryPriceInitialResponse.class);
	}

	@Override
	public boolean willDecode(String arg0) {
		return (arg0.contains("\"result\":true,\"channel\":\"")
		  && arg0.contains("_forecast_price") && !arg0.contains("index")
		  && !arg0.contains("depth") && !arg0.contains("kline")
		  && !arg0.contains("ticker") && !arg0.contains("trade"));
	}
	
}
