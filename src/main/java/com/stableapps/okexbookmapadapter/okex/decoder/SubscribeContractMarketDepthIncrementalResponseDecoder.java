/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stableapps.okexbookmapadapter.okex.decoder;

import com.stableapps.okexbookmapadapter.okex.model.SubscribeContractMarketDepthResponse;

/**
 *
 * @author aris
 */
public class SubscribeContractMarketDepthIncrementalResponseDecoder
	extends AbstractDecoder<SubscribeContractMarketDepthResponse> {

	public SubscribeContractMarketDepthIncrementalResponseDecoder() {
		super(SubscribeContractMarketDepthResponse.class);
	}

	@Override
	public boolean willDecode(String arg0) {
        boolean contains = arg0.contains("\"table\":")
                && arg0.contains("/depth\",\"action\":");
        return contains;
	}

}
