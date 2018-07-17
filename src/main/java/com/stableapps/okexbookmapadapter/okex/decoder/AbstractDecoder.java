/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stableapps.okexbookmapadapter.okex.decoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stableapps.okexbookmapadapter.okex.model.Expiration;
import com.stableapps.okexbookmapadapter.okex.model.Interval;
import java.io.IOException;
import java.util.logging.Level;
import java.util.regex.Pattern;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;
import lombok.extern.java.Log;

/**
 *
 * @author aris
 * @param <T> T is the class to convert JSON response to.
 */
@Log
public abstract class AbstractDecoder<T> implements Decoder.Text<T> {

	public static final Pattern INIT_RESPONSE_PATTERN = Pattern.compile("^.*\"result\"\\s*:\\s*true\\s*,\\s*\"channel\"\\s*:\\s*\"([^\"]+)\".*$");
	public static final Pattern RESPONSE_PATTERN = Pattern.compile("^.*\"binary\"\\s*:\\s*\\d+\\s*,\"channel\"\\s*:\\s*\"(ok_sub_futureusd_[^\"]+)\".*$");

	private static String generateExpirationPattern() {
		StringBuilder sb = new StringBuilder();
		sb.append('(');
		for (Expiration expiration : Expiration.values()) {
			sb.append(expiration.name());
			sb.append('|');
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append(')');
		return sb.toString();
	}

	private static String generateIntervalPattern() {
		StringBuilder sb = new StringBuilder();
		sb.append('(');
		for (Interval interval : Interval.values()) {
			sb.append(interval.toString());
			sb.append('|');
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append(')');
		return sb.toString();
	}

	public static final String EXPIRATION_PATTERN = generateExpirationPattern();
	public static final String INTERVAL_PATTERN = generateIntervalPattern();

	protected ObjectMapper objectMapper;
	private final Class<T> msgClass;

	public AbstractDecoder(Class<T> msgClass) {
		this.msgClass = msgClass;
	}

	@Override
	public T decode(String arg0) throws DecodeException {
//		System.out.println("\n\nResponse:\n"+arg0);
		try {
			if (arg0.startsWith("[")) {
				arg0 = arg0.substring(1, arg0.length() - 1);
			}
			return objectMapper.readValue(arg0, msgClass);
		} catch (IOException ex) {
			log.log(Level.SEVERE, "Could not decode JSON", ex);
			return null;
		}
	}

	@Override
	public abstract boolean willDecode(String arg0);

	@Override
	public void init(EndpointConfig ec) {
		objectMapper = new ObjectMapper();
	}

	@Override
	public void destroy() {
	}

}
