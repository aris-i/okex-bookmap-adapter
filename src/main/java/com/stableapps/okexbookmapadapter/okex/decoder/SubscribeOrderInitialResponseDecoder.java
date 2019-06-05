package com.stableapps.okexbookmapadapter.okex.decoder;

import com.stableapps.okexbookmapadapter.okex.model.SubscribeOrderInitialResponse;


public class SubscribeOrderInitialResponseDecoder
  extends AbstractDecoder<SubscribeOrderInitialResponse> {


	public SubscribeOrderInitialResponseDecoder() {
		super(SubscribeOrderInitialResponse.class);
	}

	@Override
	public boolean willDecode(String arg0) {
        boolean contains = arg0.contains("\"event\":\"subscribe\",\"channel\":\"")&&arg0.contains("order:");
        return contains;
	}

}
