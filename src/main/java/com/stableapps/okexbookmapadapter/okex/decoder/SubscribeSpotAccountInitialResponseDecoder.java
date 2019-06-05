package com.stableapps.okexbookmapadapter.okex.decoder;

import com.stableapps.okexbookmapadapter.okex.model.SubscribeSpotAccountInitialResponse;


public class SubscribeSpotAccountInitialResponseDecoder
  extends AbstractDecoder<SubscribeSpotAccountInitialResponse> {

	public SubscribeSpotAccountInitialResponseDecoder() {
		super(SubscribeSpotAccountInitialResponse.class);
	}

	@Override
	public boolean willDecode(String arg0) {
        boolean contains = arg0.contains("\"event\":\"subscribe\",\"channel\":\"")&&arg0.contains("spot/account:");
        return contains;
	}

}
