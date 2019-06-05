/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stableapps.okexbookmapadapter.okex.decoder;

import com.stableapps.okexbookmapadapter.okex.model.ErrorWs;

/**
 *
 * @author aris
 */
public class ErrorWsDecoder
  extends AbstractDecoder<ErrorWs> {

	public ErrorWsDecoder() {
		super(ErrorWs.class);
	}

	@Override
	public boolean willDecode(String arg0) {

//        boolean contains = arg0.contains("error")&&arg0.contains("message")&&arg0.contains("errorCode");
//        return contains;
        return false;
	}

}
