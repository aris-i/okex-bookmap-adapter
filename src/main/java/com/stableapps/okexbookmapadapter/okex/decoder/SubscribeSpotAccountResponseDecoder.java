package com.stableapps.okexbookmapadapter.okex.decoder;

import com.stableapps.okexbookmapadapter.okex.model.SubscribeSpotAccountResponse;


public class SubscribeSpotAccountResponseDecoder
  extends AbstractDecoder<SubscribeSpotAccountResponse> {

	public SubscribeSpotAccountResponseDecoder() {
		super(SubscribeSpotAccountResponse.class);
	}

	@Override
	public boolean willDecode(String arg0) {
        boolean contains = arg0.contains("\"table\":\"") && arg0.contains("spot/account\",\"data\":");
        return contains;
	}

}
