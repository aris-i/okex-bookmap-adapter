/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stableapps.okexbookmapadapter.okex.decoder;

import static com.stableapps.okexbookmapadapter.okex.decoder.AbstractDecoder.EXPIRATION_PATTERN;
import static com.stableapps.okexbookmapadapter.okex.decoder.AbstractDecoder.INIT_RESPONSE_PATTERN;
import static com.stableapps.okexbookmapadapter.okex.decoder.AbstractDecoder.INTERVAL_PATTERN;
import com.stableapps.okexbookmapadapter.okex.model.SubscribeContractCandlestickChartDataResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author aris
 */
public class SubscribeContractCandlestickChartDataResponseDecoder extends
  AbstractDecoder<SubscribeContractCandlestickChartDataResponse> {

	private static final Pattern CHANNEL_PATTERN = Pattern.compile("ok_sub_futureusd_[^_]+_kline_"
	  + EXPIRATION_PATTERN + "_" + INTERVAL_PATTERN + "$");

	public SubscribeContractCandlestickChartDataResponseDecoder() {
		super(SubscribeContractCandlestickChartDataResponse.class);
	}

	@Override
	public boolean willDecode(String arg0) {
		Matcher responseMatcher = RESPONSE_PATTERN.matcher(arg0);
		if (!responseMatcher.matches()) {
			return false;
		}
		String channel = responseMatcher.group(1);

		return CHANNEL_PATTERN.matcher(channel).matches();
	}

}
