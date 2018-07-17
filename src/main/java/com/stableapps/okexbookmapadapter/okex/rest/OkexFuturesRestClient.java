/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stableapps.okexbookmapadapter.okex.rest;

import com.stableapps.okexbookmapadapter.okex.model.CancelOrdersRequest;
import com.stableapps.okexbookmapadapter.okex.model.CancelOrdersResponse;
import com.stableapps.okexbookmapadapter.okex.model.Expiration;
import com.stableapps.okexbookmapadapter.okex.model.rest.PlaceOrderRequest;
import com.stableapps.okexbookmapadapter.okex.model.rest.PlaceOrderResponse;
import com.stableapps.okexbookmapadapter.okex.model.rest.OrderInfoRequest;
import com.stableapps.okexbookmapadapter.okex.model.rest.OrderInfoResponse;
import com.stableapps.okexbookmapadapter.okex.model.rest.PositionRequest;
import com.stableapps.okexbookmapadapter.okex.model.rest.PositionRequestResponse;
import com.stableapps.okexbookmapadapter.okex.model.rest.UserInfoResponse;
import com.stableapps.okexbookmapadapter.okex.utils.SignForm;
import javax.ws.rs.core.Form;
import lombok.extern.log4j.Log4j;

/**
 *
 * @author aris
 */
@Log4j
public class OkexFuturesRestClient extends AbstractRestClient {
	private static final int MAX_RETRY = 5;

	public OkexFuturesRestClient(String apiKey, String secretKey) {
		super("https://www.okex.com/api/v1/", apiKey, secretKey);
	}

	public CancelOrdersResponse cancelOrders(CancelOrdersRequest cancelOrdersRequest) {
		Form form = new Form();

		form.param("api_key", apiKey);

		// cancel orders request parameters
		form.param("symbol", cancelOrdersRequest.getSymbol());
		form.param("order_id", cancelOrdersRequest.getOrderIds());
		form.param("contract_type", cancelOrdersRequest.getExpiration().name());

		SignForm signForm = new SignForm();
		String sign = signForm.sign(form, secretKey);
		form.param("sign", sign);

		return (CancelOrdersResponse) call("future_cancel.do", form, CancelOrdersResponse.class);
	}

	public PlaceOrderResponse placeOrder(PlaceOrderRequest order) {
		Form form = new Form();

		form.param("api_key", apiKey);
		// Order request parameters
		form.param("symbol", order.getSymbol());
		form.param("contract_type", order.getExpiration().name());
		form.param("price", String.valueOf(order.getPrice()));
		form.param("amount", String.valueOf(order.getAmount()));
		form.param("type", String.valueOf(order.getType()));
		form.param("match_price", String.valueOf(order.getMatchPrice()));
		form.param("lever_rate", String.valueOf(order.getLeverRate()));

		SignForm signForm = new SignForm();
		String sign = signForm.sign(form, secretKey);
		form.param("sign", sign);

		return call("future_trade.do", form, PlaceOrderResponse.class);

	}

	public PositionRequestResponse fetchPosition(PositionRequest positionRequest) {
		Form form = new Form();

		form.param("api_key", apiKey);
		form.param("symbol", positionRequest.getSymbol());
		form.param("contract_type", positionRequest.getExpiration().name());
//		form.param("type", String.valueOf(1));

		SignForm signForm = new SignForm();
		String sign = signForm.sign(form, secretKey);
		form.param("sign", sign);

		return call("future_position_4fix.do", form, PositionRequestResponse.class
		);

	}

	public OrderInfoResponse fetchOrdersInfo(OrderInfoRequest orderInfoRequest) {
		Form form = new Form();

		form.param("api_key", apiKey);
		form.param("symbol", orderInfoRequest.getSymbol());
		form.param("contract_type", orderInfoRequest.getExpiration().name());
		form.param("status", orderInfoRequest.getStatus().value());
		form.param("order_id", orderInfoRequest.getOrderId());
		form.param("current_page", orderInfoRequest.getCurrentPage());
		form.param("page_length", orderInfoRequest.getPageLength());

		SignForm signForm = new SignForm();
		String sign = signForm.sign(form, secretKey);
		form.param("sign", sign);

		return call("future_order_info.do", form, OrderInfoResponse.class);

	}

	public UserInfoResponse fetchUserInfo() {
		Form form = new Form();
		form.param("api_key", apiKey);

		SignForm signForm = new SignForm();
		String sign = signForm.sign(form, secretKey);
		form.param("sign", sign);

		return call("future_userinfo_4fix.do", form, UserInfoResponse.class);

	}

	private <R> R call(String path, Form form, Class<R> responseClass) {
		int retry = 0;
		while (retry < MAX_RETRY) {
			try {
				R response = getWebTarget().path(path)
					.request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
					.post(
						javax.ws.rs.client.Entity.entity(
							form,
							javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED
						), responseClass
					);

				log.info(String.format("Rest call to %s successful", path));
				return response;
			} catch (Exception e) {
				log.warn(String.format("Rest call to %s failed: %s", path, e.getMessage()));
				try {
					retry++;
					log.info(String.format("Retrying rest call to %s n times: %d", path, retry));
					Thread.sleep(retry * 1000);
				} catch (InterruptedException ex) {
					log.info("Interrupted while trying to retry rest call");
					return null;
				}
			}
		}

		log.info( "Giving up calling REST: " +path);

		try {
			return responseClass.newInstance();
		} catch (InstantiationException | IllegalAccessException ex) {
			throw new RuntimeException("Can't instantiate a blank response class");
		}
	}

	public static void main(String[] args) {
		//Try fetching position
		String apiKey = "d7086a54-4080-4a2b-bb9b-d178912b1b5f";
		String secretKey = "7F2F116B07679A1ABC5AC5B2468133BA";
		try (OkexFuturesRestClient client = new OkexFuturesRestClient(apiKey, secretKey)) {
			OrderInfoResponse fetchOrdersInfo = client.fetchOrdersInfo(OrderInfoRequest.builder().symbol("btc_usd").expiration(Expiration.quarter).build()
			);
			System.out.println("fetchOrdersInfo: " + fetchOrdersInfo);
			PositionRequestResponse fetchPosition = client.fetchPosition(PositionRequest.builder().symbol("btc_usd").expiration(Expiration.quarter).build());
			System.out.println("fetchPosition: " + fetchPosition);
		}
	}

}
