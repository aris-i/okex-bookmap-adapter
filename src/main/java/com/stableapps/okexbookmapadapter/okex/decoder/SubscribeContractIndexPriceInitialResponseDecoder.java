/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stableapps.okexbookmapadapter.okex.decoder;

import static com.stableapps.okexbookmapadapter.okex.decoder.AbstractDecoder.INIT_RESPONSE_PATTERN;
import com.stableapps.okexbookmapadapter.okex.model.SubscribeContractIndexPriceInitialResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author aris
 */
public class SubscribeContractIndexPriceInitialResponseDecoder
  extends AbstractDecoder<SubscribeContractIndexPriceInitialResponse> {

	private static final Pattern CHANNEL_PATTERN = Pattern.compile("ok_sub_futureusd_[^_]+_index$");

	public SubscribeContractIndexPriceInitialResponseDecoder() {
		super(SubscribeContractIndexPriceInitialResponse.class);
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
