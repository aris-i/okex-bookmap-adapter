/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stableapps.okexbookmapadapter.okex.decoder;

import com.stableapps.okexbookmapadapter.okex.model.SubscribeContractTradeRecordResponse;

/**
 *
 * @author aris
 */
public class SubscribeContractTradeRecordResponseDecoder
  extends AbstractDecoder<SubscribeContractTradeRecordResponse> {

	public SubscribeContractTradeRecordResponseDecoder() {
		super(SubscribeContractTradeRecordResponse.class);
	}

	@Override
	public boolean willDecode(String arg0) {
        boolean contains = arg0.contains("\"table\":") && arg0.contains("/trade");
        return contains;
	}

}
