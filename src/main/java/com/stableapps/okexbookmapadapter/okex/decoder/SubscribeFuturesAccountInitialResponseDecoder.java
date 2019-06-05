package com.stableapps.okexbookmapadapter.okex.decoder;

import com.stableapps.okexbookmapadapter.okex.model.SubscribeFuturesAccountInitialResponse;

public class SubscribeFuturesAccountInitialResponseDecoder
  extends AbstractDecoder<SubscribeFuturesAccountInitialResponse> {

	public SubscribeFuturesAccountInitialResponseDecoder() {
		super(SubscribeFuturesAccountInitialResponse.class);
	}

	@Override
	public boolean willDecode(String arg0) {
        boolean contains = arg0.contains("\"event\":\"subscribe\",\"channel\":\"")&&arg0.contains("futures/account:");
        return contains;
	}

}
