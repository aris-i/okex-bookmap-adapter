package com.stableapps.okexbookmapadapter.okex.utils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.ws.rs.core.Form;
import org.apache.commons.codec.digest.DigestUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 * @author aris
 */
public class SignForm {

    private final ObjectMapper objectMapper = new ObjectMapper();
    
	public String sign(Form form, String secretKey) {
		LinkedHashMap<String, String> map = new LinkedHashMap<>();
		form.asMap().entrySet().forEach((Map.Entry<String, List<String>> entry) -> {
			String key = entry.getKey();
			String value = entry.getValue().get(0);
			if (key.equals("sign")) {
				return;
			}
			map.put(key, value);
		});
//		StringBuilder sb = new StringBuilder();
//		map.entrySet().forEach((Map.Entry<String, String> entry) -> {
//			sb.append(entry.getKey()).append('=').append(entry.getValue()).append('&');
//		});
//		sb.append("secret_key=").append(secretKey);
//		return DigestUtils.md5Hex(sb.toString()).toUpperCase();
		
		try {
            String json = objectMapper.writeValueAsString(map);
            return json;
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		return null;
	}

}
