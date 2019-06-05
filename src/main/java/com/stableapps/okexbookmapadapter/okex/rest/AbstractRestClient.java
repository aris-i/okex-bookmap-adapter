/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stableapps.okexbookmapadapter.okex.rest;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import lombok.Getter;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;

/**
 *
 * @author aris
 */
public class AbstractRestClient implements AutoCloseable {

	@Getter
//	private final WebTarget webTarget;
	private WebTarget webTarget;
	@Getter
	private final Client client;
	protected final String apiKey;
	protected final String secretKey;

	public AbstractRestClient(String baseUrl, String apiKey, String secretKey) {
		client = javax.ws.rs.client.ClientBuilder.newClient(new ClientConfig(JacksonJaxbJsonProvider.class));
		client.property(ClientProperties.CONNECT_TIMEOUT, 1000_000);
		client.property(ClientProperties.READ_TIMEOUT, 1000_000);
//		client.register(new LoggingFilter(log, true));
		webTarget = client.target(baseUrl);
		this.apiKey = apiKey;
		this.secretKey = secretKey;
	}

	@Override
	public void close() {
		getClient().close();
	}

}
