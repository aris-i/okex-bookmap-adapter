/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stableapps.okexbookmapadapter.okex.decoder;

import com.stableapps.okexbookmapadapter.okex.model.SubscribeContractTradeRecordInitialResponse;

/**
 *
 * @author aris
 */
public class SubscribeContractTradeRecordInitialResponseDecoder
  extends AbstractDecoder<SubscribeContractTradeRecordInitialResponse> {

	public SubscribeContractTradeRecordInitialResponseDecoder() {
		super(SubscribeContractTradeRecordInitialResponse.class);
	}

	@Override
	public boolean willDecode(String arg0) {
        boolean contains = arg0.contains("\"event\":\"subscribe\",\"channel\":\"") && arg0.contains("trade:");
        return contains;
	}

}
