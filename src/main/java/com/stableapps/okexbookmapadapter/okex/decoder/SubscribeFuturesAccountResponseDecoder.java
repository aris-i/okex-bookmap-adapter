package com.stableapps.okexbookmapadapter.okex.decoder;

import com.stableapps.okexbookmapadapter.okex.model.SubscribeFuturesAccountResponse;


public class SubscribeFuturesAccountResponseDecoder
  extends AbstractDecoder<SubscribeFuturesAccountResponse> {

	public SubscribeFuturesAccountResponseDecoder() {
		super(SubscribeFuturesAccountResponse.class);
	}

	@Override
	public boolean willDecode(String arg0) {
        boolean contains = arg0.contains("\"table\":\"futures/account\",\"data\":");
        return contains;
	}

}
