/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stableapps.okexbookmapadapter.okex.decoder;

import com.stableapps.okexbookmapadapter.okex.model.SubscribeContractMarketDepthInitialResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author aris
 */
public class SubscribeContractMarketDepthFullInitialResponseDecoder
  extends AbstractDecoder<SubscribeContractMarketDepthInitialResponse> {

	private static final Pattern CHANNEL_PATTERN = Pattern.compile("ok_sub_futureusd_[^_]+_depth_"
	  + EXPIRATION_PATTERN + "_\\d+$");

	public SubscribeContractMarketDepthFullInitialResponseDecoder() {
		super(SubscribeContractMarketDepthInitialResponse.class);
	}

	@Override
	public boolean willDecode(String arg0) {
		Matcher initResponseMatcher = INIT_RESPONSE_PATTERN.matcher(arg0);
		if (!initResponseMatcher.matches()) {
			return false;
		}
		String channel = initResponseMatcher.group(1);

		return CHANNEL_PATTERN.matcher(channel).matches();
	}

}
