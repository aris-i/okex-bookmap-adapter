package com.stableapps.okexbookmapadapter.okex.decoder;

import com.stableapps.okexbookmapadapter.okex.model.SubscribeFuturesPositionResponse;


public class SubscribePositionFuturesDecoder
  extends AbstractDecoder<SubscribeFuturesPositionResponse> {

	public SubscribePositionFuturesDecoder() {
		super(SubscribeFuturesPositionResponse.class);
	}

	@Override
	public boolean willDecode(String arg0) {
        boolean contains = arg0.contains("\"table\":\"futures/position\",\"data\":[");
        return contains;
	}

}
