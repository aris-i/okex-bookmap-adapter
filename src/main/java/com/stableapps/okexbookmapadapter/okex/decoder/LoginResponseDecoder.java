/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stableapps.okexbookmapadapter.okex.decoder;

import com.stableapps.okexbookmapadapter.okex.model.LoginResponse;

/**
 *
 * @author aris
 */
public class LoginResponseDecoder extends AbstractDecoder<LoginResponse> {

	public LoginResponseDecoder() {
		super(LoginResponse.class);
	}

	@Override
	public boolean willDecode(String arg0) {
	    boolean isValid = arg0.contains("\"event\":\"login\",")
	            || arg0.contains("\"errorCode\":30013")
	            || arg0.contains("\"errorCode\":30027")
	                    ; 
		return isValid;
	}

}
