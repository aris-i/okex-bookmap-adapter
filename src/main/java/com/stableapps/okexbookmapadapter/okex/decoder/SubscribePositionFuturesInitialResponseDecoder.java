package com.stableapps.okexbookmapadapter.okex.decoder;

import com.stableapps.okexbookmapadapter.okex.model.SubscribeFuturesPositionInitialResponse;


public class SubscribePositionFuturesInitialResponseDecoder
  extends AbstractDecoder<SubscribeFuturesPositionInitialResponse> {

	public SubscribePositionFuturesInitialResponseDecoder() {
		super(SubscribeFuturesPositionInitialResponse.class);
	}

	@Override
	public boolean willDecode(String arg0) {
        boolean contains = arg0.contains("\"event\":\"subscribe\",\"channel\":\"futures/position:");
        return contains;
	}

}
