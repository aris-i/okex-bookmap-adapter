/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stableapps.okexbookmapadapter.okex.decoder;

import static com.stableapps.okexbookmapadapter.okex.decoder.AbstractDecoder.RESPONSE_PATTERN;
import com.stableapps.okexbookmapadapter.okex.model.SubscribeContractUserPositionsFixedMarginResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author aris
 */
public class SubscribeContractUserPositionsFixedMarginResponseDecoder
		extends AbstractDecoder<SubscribeContractUserPositionsFixedMarginResponse> {

	private static final Pattern CHANNEL_PATTERN = Pattern.compile("ok_sub_futureusd_positions$");

	public SubscribeContractUserPositionsFixedMarginResponseDecoder() {
		super(SubscribeContractUserPositionsFixedMarginResponse.class);
	}

	@Override
	public boolean willDecode(String arg0) {
		Matcher responseMatcher = RESPONSE_PATTERN.matcher(arg0);
		if (!responseMatcher.matches()) {
			return false;
		}
		if (!arg0.contains("fixmargin")) {
			return false;
		}

		String channel = responseMatcher.group(1);

		return CHANNEL_PATTERN.matcher(channel).matches();
	}

}
