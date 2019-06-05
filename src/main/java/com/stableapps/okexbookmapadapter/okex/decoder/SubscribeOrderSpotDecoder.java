package com.stableapps.okexbookmapadapter.okex.decoder;

import com.stableapps.okexbookmapadapter.okex.model.SubscribeOrderSpot;


public class SubscribeOrderSpotDecoder
  extends AbstractDecoder<SubscribeOrderSpot> {

	public SubscribeOrderSpotDecoder() {
		super(SubscribeOrderSpot.class);
	}

	@Override
	public boolean willDecode(String arg0) {
        boolean contains = arg0.contains("table\":\"spot/order\",\"data\":[{\"client_oid\":");
        return contains;
	}

}
