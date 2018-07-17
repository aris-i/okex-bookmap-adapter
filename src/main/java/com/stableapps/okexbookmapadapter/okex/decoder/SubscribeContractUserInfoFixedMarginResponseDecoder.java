package com.stableapps.okexbookmapadapter.okex.decoder;

import static com.stableapps.okexbookmapadapter.okex.decoder.AbstractDecoder.RESPONSE_PATTERN;
import com.stableapps.okexbookmapadapter.okex.model.SubscribeContractsUserInfoFixedMarginResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author aris
 */
public class SubscribeContractUserInfoFixedMarginResponseDecoder
		extends AbstractDecoder<SubscribeContractsUserInfoFixedMarginResponse> {

	private static final Pattern CHANNEL_PATTERN = Pattern.compile("ok_sub_futureusd_userinfo$");

	public SubscribeContractUserInfoFixedMarginResponseDecoder() {
		super(SubscribeContractsUserInfoFixedMarginResponse.class);
	}

	@Override
	public boolean willDecode(String arg0) {
		Matcher responseMatcher = RESPONSE_PATTERN.matcher(arg0);
		if (!responseMatcher.matches()) {
			return false;
		}
		if (!arg0.contains("bond")) {
			return false;
		}

		String channel = responseMatcher.group(1);

		return CHANNEL_PATTERN.matcher(channel).matches();
	}

}
