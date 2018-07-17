/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
