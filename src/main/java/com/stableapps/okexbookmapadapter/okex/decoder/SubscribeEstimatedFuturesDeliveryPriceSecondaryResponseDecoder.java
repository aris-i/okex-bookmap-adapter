/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stableapps.okexbookmapadapter.okex.decoder;

import com.stableapps.okexbookmapadapter.okex.model.SubscribeEstimatedFuturesDeliveryPriceSecondaryResponse;

/**
 *
 * @author aris
 */
public class SubscribeEstimatedFuturesDeliveryPriceSecondaryResponseDecoder
  extends AbstractDecoder<SubscribeEstimatedFuturesDeliveryPriceSecondaryResponse> {

	public SubscribeEstimatedFuturesDeliveryPriceSecondaryResponseDecoder() {
		super(SubscribeEstimatedFuturesDeliveryPriceSecondaryResponse.class);
	}

	@Override
	public boolean willDecode(String arg0) {
		return (arg0.contains("{\"binary\":0,\"channel\":\"")
		  && arg0.contains("_forecast_price\",\"data\":\"") && !arg0.contains("index")
		  && !arg0.contains("depth") && !arg0.contains("kline")
		  && !arg0.contains("ticker") && !arg0.contains("trade"));
	}

}
