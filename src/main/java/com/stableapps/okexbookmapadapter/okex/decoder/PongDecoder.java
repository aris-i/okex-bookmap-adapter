package com.stableapps.okexbookmapadapter.okex.decoder;

import com.stableapps.okexbookmapadapter.okex.model.Pong;

/**
 *
 * @author aris
 */
public class PongDecoder extends AbstractDecoder<Pong>{

	public PongDecoder() {
		super(Pong.class);
	}


	@Override
	public boolean willDecode(String arg0) {
		return arg0.startsWith("{\"event\":\"pong\"");
	}

	
}
