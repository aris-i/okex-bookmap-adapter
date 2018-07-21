/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stableapps.okexbookmapadapter.okex;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stableapps.okexbookmapadapter.okex.decoder.PongDecoder;
import com.stableapps.okexbookmapadapter.okex.decoder.StringMessageDecoder;
import com.stableapps.okexbookmapadapter.okex.decoder.SubscribeContractMarketPriceInitialResponseDecoder;
import com.stableapps.okexbookmapadapter.okex.decoder.SubscribeContractMarketPriceResponseDecoder;
import com.stableapps.okexbookmapadapter.okex.decoder.SubscribeContractCandlestickChartDataInitialResponseDecoder;
import com.stableapps.okexbookmapadapter.okex.decoder.SubscribeContractCandlestickChartDataResponseDecoder;
import com.stableapps.okexbookmapadapter.okex.decoder.SubscribeContractMarketDepthIncrementalResponseDecoder;
import com.stableapps.okexbookmapadapter.okex.decoder.SubscribeContractMarketDepthIncrementalInitialResponseDecoder;
import com.stableapps.okexbookmapadapter.okex.decoder.SubscribeContractMarketDepthFullInitialResponseDecoder;
import com.stableapps.okexbookmapadapter.okex.decoder.SubscribeContractMarketDepthFullResponseDecoder;
import com.stableapps.okexbookmapadapter.okex.decoder.SubscribeContractIndexPriceInitialResponseDecoder;
import com.stableapps.okexbookmapadapter.okex.decoder.SubscribeContractIndexPriceResponseDecoder;

import com.stableapps.okexbookmapadapter.okex.decoder.SubscribeContractTradeRecordInitialResponseDecoder;
import com.stableapps.okexbookmapadapter.okex.decoder.SubscribeContractTradeRecordResponseDecoder;
import com.stableapps.okexbookmapadapter.okex.decoder.SubscribeEstimatedFuturesDeliveryPriceInitialResponseDecoder;
import com.stableapps.okexbookmapadapter.okex.decoder.SubscribeEstimatedFuturesDeliveryPriceSecondaryResponseDecoder;
import com.stableapps.okexbookmapadapter.okex.decoder.SubscribeEstimatedFuturesDeliveryPriceResponseDecoder;
import com.stableapps.okexbookmapadapter.okex.decoder.LoginResponseDecoder;
import com.stableapps.okexbookmapadapter.okex.decoder.SubscribeContractTradeRecordsResponseDecoder;
import com.stableapps.okexbookmapadapter.okex.decoder.SubscribeContractUserInfoFixedMarginResponseDecoder;
import com.stableapps.okexbookmapadapter.okex.decoder.SubscribeContractUserPositionsFixedMarginResponseDecoder;
import com.stableapps.okexbookmapadapter.okex.decoder.UnsubscribeResponseDecoder;
import com.stableapps.okexbookmapadapter.okex.model.Pong;
import com.stableapps.okexbookmapadapter.okex.model.Event;
import com.stableapps.okexbookmapadapter.okex.model.Message;
import com.stableapps.okexbookmapadapter.okex.model.Expiration;
import com.stableapps.okexbookmapadapter.okex.model.Interval;
import com.stableapps.okexbookmapadapter.okex.model.MarketPrice;
import com.stableapps.okexbookmapadapter.okex.model.SubscribeContractMarketPriceInitialResponse;
import com.stableapps.okexbookmapadapter.okex.model.SubscribeContractMarketPriceResponse;
import com.stableapps.okexbookmapadapter.okex.model.CandlestickChartData;
import com.stableapps.okexbookmapadapter.okex.model.SubscribeContractCandlestickChartDataInitialResponse;
import com.stableapps.okexbookmapadapter.okex.model.SubscribeContractCandlestickChartDataResponse;
import com.stableapps.okexbookmapadapter.okex.model.MarketDepths;
import com.stableapps.okexbookmapadapter.okex.model.SubscribeContractMarketDepthInitialResponse;
import com.stableapps.okexbookmapadapter.okex.model.SubscribeContractMarketDepthResponse;
import com.stableapps.okexbookmapadapter.okex.model.Trade;
import com.stableapps.okexbookmapadapter.okex.model.SubscribeContractTradeRecordInitialResponse;
import com.stableapps.okexbookmapadapter.okex.model.SubscribeContractTradeRecordResponse;
import com.stableapps.okexbookmapadapter.okex.model.IndexPrice;
import com.stableapps.okexbookmapadapter.okex.model.SubscribeContractIndexPriceInitialResponse;
import com.stableapps.okexbookmapadapter.okex.model.SubscribeContractIndexPriceResponse;
import com.stableapps.okexbookmapadapter.okex.model.ForecastPrice;
import com.stableapps.okexbookmapadapter.okex.model.SubscribeEstimatedFuturesDeliveryPriceInitialResponse;
import com.stableapps.okexbookmapadapter.okex.model.SubscribeEstimatedFuturesDeliveryPriceSecondaryResponse;
import com.stableapps.okexbookmapadapter.okex.model.SubscribeEstimatedFuturesDeliveryPriceResponse;
import com.stableapps.okexbookmapadapter.okex.model.LoginResponse;
import com.stableapps.okexbookmapadapter.okex.model.SubscribeContractTradeRecordsResponse;
import com.stableapps.okexbookmapadapter.okex.model.SubscribeContractsUserInfoFixedMarginResponse;
import com.stableapps.okexbookmapadapter.okex.model.SubscribeContractUserPositionsFixedMarginResponse;
import com.stableapps.okexbookmapadapter.okex.model.UnsubscribeResponse;
import com.stableapps.okexbookmapadapter.okex.model.Contracts;
import com.stableapps.okexbookmapadapter.okex.model.Order;
import com.stableapps.okexbookmapadapter.okex.model.rest.PlaceOrderRequest;
import com.stableapps.okexbookmapadapter.okex.model.rest.PlaceOrderResponse;
import com.stableapps.okexbookmapadapter.okex.model.CancelOrdersRequest;
import com.stableapps.okexbookmapadapter.okex.model.CancelOrdersResponse;
import com.stableapps.okexbookmapadapter.okex.model.PositionsFixedMargin;
import com.stableapps.okexbookmapadapter.okex.model.rest.OrderInfoRequest;
import com.stableapps.okexbookmapadapter.okex.model.rest.OrderInfoResponse;
import com.stableapps.okexbookmapadapter.okex.model.rest.PositionRequest;
import com.stableapps.okexbookmapadapter.okex.model.rest.PositionRequestResponse;
import com.stableapps.okexbookmapadapter.okex.model.rest.UserInfoResponse;
import com.stableapps.okexbookmapadapter.okex.rest.OkexFuturesRestClient;
import com.stableapps.okexbookmapadapter.okex.utils.SignForm;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.websocket.ClientEndpointConfig;
import javax.websocket.CloseReason;
import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;
import javax.ws.rs.core.Form;
import lombok.extern.log4j.Log4j;
import org.glassfish.tyrus.client.ClientManager;
import org.glassfish.tyrus.client.ClientProperties;

/**
 *
 * @author aris
 */
@Log4j
public class OkexConnector extends Endpoint implements AutoCloseable {

	private static final int HEART_BEAT_INTERVAL = 30000;//30 seconds
	private static final Pattern MARKET_DEPTH_CHANNEL = Pattern.compile("^ok_sub_futureusd_([^_]+)_depth_([^\\d]+)(_\\d+)?$");
	private static final Pattern CONTRACT_MARKET_PRICE_CHANNEL = Pattern.compile("^ok_sub_futureusd_([^_]+)_ticker_(.*)$");
	private static final Pattern CONTRACT_TRADE_RECORD_CHANNEL = Pattern.compile("^ok_sub_futureusd_([^_]+)_trade_(.*)$");

	private Session session;
	private Timer timer;
	private final ReentrantReadWriteLock.ReadLock rLock;
	private final ReentrantReadWriteLock.WriteLock wLock;
	private final Condition newPong;
	private Pong pong;
	private final ObjectMapper objectMapper;
	private final Condition newSubscribeContractMarketPriceInitialResponse;
	private final Condition newSubscribeContractCandlestickChartDataInitialResponse;
	private final Condition newSubscribeContractMarketDepthInitialResponse;
	private final Condition newSubscribeContractTradeRecordInitialResponse;
	private final Condition newSubscribeContractIndexPriceInitialResponse;
	private final Condition newSubscribeEstimatedFuturesDeliveryPriceInitialResponse;
	private final Condition newLoginResponse;
	private final Condition newUnsubscribeResponse;
	private SubscribeContractMarketPriceInitialResponse subscribeContractMarketPriceInitialResponse;
	private SubscribeContractCandlestickChartDataInitialResponse subscribeContractCandlestickChartDataInitialResponse;
	private SubscribeContractMarketDepthInitialResponse subscribeContractMarketDepthInitialResponse;
	private SubscribeContractTradeRecordInitialResponse subscribeContractTradeRecordInitialResponse;
	private SubscribeContractIndexPriceInitialResponse subscribeContractIndexPriceInitialResponse;
	private SubscribeEstimatedFuturesDeliveryPriceInitialResponse subscribeEstimatedFuturesDeliveryPriceInitialResponse;
	private LoginResponse loginResponse;
	private UnsubscribeResponse unsubscribeResponse;

	private final AbstractClient client;
	private boolean reconnecting;
	private final String apiKey;
	private final String secretKey;
	private final int leverRate;
	protected final OkexFuturesRestClient okexRestClient;
	private final ExecutorService singleThreadExecutor;

	public OkexConnector(String apiKey, String secretKey, int leverRate, AbstractClient client) {
		this.apiKey = apiKey;
		this.secretKey = secretKey;
		this.leverRate = leverRate;
		ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
		rLock = rwLock.readLock();
		wLock = rwLock.writeLock();
		newPong = wLock.newCondition();
		newSubscribeContractMarketPriceInitialResponse = wLock.newCondition();
		newSubscribeContractCandlestickChartDataInitialResponse = wLock.newCondition();
		newSubscribeContractMarketDepthInitialResponse = wLock.newCondition();
		newSubscribeContractTradeRecordInitialResponse = wLock.newCondition();
		newSubscribeContractIndexPriceInitialResponse = wLock.newCondition();
		newSubscribeEstimatedFuturesDeliveryPriceInitialResponse = wLock.newCondition();
		newLoginResponse = wLock.newCondition();
		newUnsubscribeResponse = wLock.newCondition();
		objectMapper = new ObjectMapper();
		this.client = client;
		this.okexRestClient = new OkexFuturesRestClient(apiKey, secretKey);

		singleThreadExecutor = Executors.newSingleThreadExecutor();
	}

	/*
	** The next 7 requests are implementations of Contract Price API.
	** This will received the latest OKEX Contract Data. 
	 */
	public boolean subscribeContractMarketPrice(String symbol, Expiration expiration) {
		try {
			HashMap<String, String> params = new HashMap<>();
			params.put("event", Event.addChannel.name());
			final String channel = "ok_sub_futureusd_X_ticker_Y".replace("X", symbol)
					.replace("Y", expiration.name());
			params.put("channel", channel);
			String json = objectMapper.writeValueAsString(params);
			getSession().getBasicRemote().sendText(json);
			SubscribeContractMarketPriceInitialResponse response
					= waitFor(SubscribeContractMarketPriceInitialResponse.class);

			if (!response.getData().isResult()) {
				return false;
			}
			return true;
		} catch (JsonProcessingException ex) {
			log.fatal("Error converting to JSON", ex);
			return false;
		} catch (IOException ex) {
			log.fatal("Error sending request to OKEx", ex);
			return false;
		}
	}

	public boolean unsubscribeContractMarketPrice(String symbol, Expiration expiration) {
		try {
			HashMap<String, String> params = new HashMap<>();
			params.put("event", Event.removeChannel.name());
			final String channel = "ok_sub_futureusd_X_ticker_Y".replace("X", symbol)
					.replace("Y", expiration.name());
			params.put("channel", channel);
			String json = objectMapper.writeValueAsString(params);
			getSession().getBasicRemote().sendText(json);
			UnsubscribeResponse response
					= waitFor(UnsubscribeResponse.class);

			if (!response.getData().isResult()) {
				return false;
			}
			return true;
		} catch (JsonProcessingException ex) {
			log.fatal("Error converting to JSON", ex);
			return false;
		} catch (IOException ex) {
			log.fatal("Error sending request to OKEx", ex);
			return false;
		}
	}

	public boolean subscribeContractCandlestickChartData(String symbol, Expiration expiration,
			Interval interval) {
		try {
			HashMap<String, String> params = new HashMap<>();
			params.put("event", Event.addChannel.name());
			final String channel = "ok_sub_futureusd_X_kline_Y_Z".replace("X", symbol)
					.replace("Y", expiration.name()).replace("Z", interval.toString());
			params.put("channel", channel);
			String json = objectMapper.writeValueAsString(params);
			getSession().getBasicRemote().sendText(json);
			SubscribeContractCandlestickChartDataInitialResponse response
					= waitFor(SubscribeContractCandlestickChartDataInitialResponse.class);

			if (!response.getData().isResult()) {
				return false;
			}
			return true;
		} catch (JsonProcessingException ex) {
			log.fatal("Error converting to JSON", ex);
			return false;
		} catch (IOException ex) {
			log.fatal("Error sending request to OKEx", ex);
			return false;
		}

	}

	public boolean subscribeContractMarketDepthIncremental(String symbol, Expiration expiration) {
		try {
			HashMap<String, String> params = new HashMap<>();
			params.put("event", Event.addChannel.name());
			final String channel = "ok_sub_futureusd_X_depth_Y".replace("X", symbol)
					.replace("Y", expiration.name());
			params.put("channel", channel);
			String json = objectMapper.writeValueAsString(params);
			getSession().getBasicRemote().sendText(json);
			SubscribeContractMarketDepthInitialResponse response
					= waitFor(SubscribeContractMarketDepthInitialResponse.class);

			if (!response.getData().isResult()) {
				return false;
			}
			return true;
		} catch (JsonProcessingException ex) {
			log.fatal("Error converting to JSON", ex);
			return false;
		} catch (IOException ex) {
			log.fatal("Error sending request to OKEx", ex);
			return false;
		}
	}

	public boolean subscribeContractMarketDepthFull(String symbol, Expiration expiration, int amount) {
		try {
			HashMap<String, String> params = new HashMap<>();
			params.put("event", Event.addChannel.name());
			final String channel = "ok_sub_futureusd_X_depth_Y_Z".replace("X", symbol)
					.replace("Y", expiration.name()).replace("Z", String.valueOf(amount));
			params.put("channel", channel);
			String json = objectMapper.writeValueAsString(params);
			getSession().getBasicRemote().sendText(json);
			SubscribeContractMarketDepthInitialResponse response
					= waitFor(SubscribeContractMarketDepthInitialResponse.class);

			if (!response.getData().isResult()) {
				return false;
			}
			return true;
		} catch (JsonProcessingException ex) {
			log.fatal("Error converting to JSON", ex);
			return false;
		} catch (IOException ex) {
			log.fatal("Error sending request to OKEx", ex);
			return false;
		}
	}

	public boolean unsubscribeContractMarketDepthFull(String symbol, Expiration expiration, int amount) {
		try {
			HashMap<String, String> params = new HashMap<>();
			params.put("event", Event.removeChannel.name());
			final String channel = "ok_sub_futureusd_X_depth_Y_Z".replace("X", symbol)
					.replace("Y", expiration.name()).replace("Z", String.valueOf(amount));
			params.put("channel", channel);
			String json = objectMapper.writeValueAsString(params);
			getSession().getBasicRemote().sendText(json);
			UnsubscribeResponse response
					= waitFor(UnsubscribeResponse.class);

			if (!response.getData().isResult()) {
				return false;
			}
			return true;
		} catch (JsonProcessingException ex) {
			log.fatal("Error converting to JSON", ex);
			return false;
		} catch (IOException ex) {
			log.fatal("Error sending request to OKEx", ex);
			return false;
		}
	}

	// subscribe to existing trades
	public boolean subscribeContractTradeRecord(String symbol, Expiration expiration) {
		try {
			HashMap<String, String> params = new HashMap<>();
			params.put("event", Event.addChannel.name());
			final String channel = "ok_sub_futureusd_X_trade_Y".replace("X", symbol)
					.replace("Y", expiration.name());
			params.put("channel", channel);
			String json = objectMapper.writeValueAsString(params);
			getSession().getBasicRemote().sendText(json);
			SubscribeContractTradeRecordInitialResponse response
					= waitFor(SubscribeContractTradeRecordInitialResponse.class);

			if (!response.getData().isResult()) {
				return false;
			}
			return true;
		} catch (JsonProcessingException ex) {
			log.fatal("Error converting to JSON", ex);
			return false;
		} catch (IOException ex) {
			log.fatal("Error sending request to OKEx", ex);
			return false;
		}
	}

	public boolean unsubscribeContractTradeRecord(String symbol, Expiration expiration) {
		try {
			HashMap<String, String> params = new HashMap<>();
			params.put("event", Event.removeChannel.name());
			final String channel = "ok_sub_futureusd_X_trade_Y".replace("X", symbol)
					.replace("Y", expiration.name());
			params.put("channel", channel);
			String json = objectMapper.writeValueAsString(params);
			getSession().getBasicRemote().sendText(json);
			UnsubscribeResponse response = waitFor(UnsubscribeResponse.class);

			if (!response.getData().isResult()) {
				return false;
			}
			return true;
		} catch (JsonProcessingException ex) {
			log.fatal("Error converting to JSON", ex);
			return false;
		} catch (IOException ex) {
			log.fatal("Error sending request to OKEx", ex);
			return false;
		}
	}

	public boolean subscribeContractIndexPrice(String symbol) {
		try {
			HashMap<String, String> params = new HashMap<>();
			params.put("event", Event.addChannel.name());
			final String channel = "ok_sub_futureusd_X_index".replace("X", symbol);
			params.put("channel", channel);
			String json = objectMapper.writeValueAsString(params);
			getSession().getBasicRemote().sendText(json);
			SubscribeContractIndexPriceInitialResponse response
					= waitFor(SubscribeContractIndexPriceInitialResponse.class);

			if (!response.getData().isResult()) {
				return false;
			}
			return true;
		} catch (JsonProcessingException ex) {
			log.fatal("Error converting to JSON", ex);
			return false;
		} catch (IOException ex) {
			log.fatal("Error sending request to OKEx", ex);
			return false;
		}
	}

	public boolean subscribeEstimatedFuturesDeliveryPrice(String symbol) {
		try {
			HashMap<String, String> params = new HashMap<>();
			params.put("event", Event.addChannel.name());
			final String channel = "X_forecast_price".replace("X", symbol);
			params.put("channel", channel);
			String json = objectMapper.writeValueAsString(params);
			getSession().getBasicRemote().sendText(json);
			SubscribeEstimatedFuturesDeliveryPriceInitialResponse response
					= waitFor(SubscribeEstimatedFuturesDeliveryPriceInitialResponse.class);

			if (!response.getData().isResult()) {
				return false;
			}
			return true;
		} catch (JsonProcessingException ex) {
			log.fatal("Error converting to JSON", ex);
			return false;
		} catch (IOException ex) {
			log.fatal("Error sending request to OKEx", ex);
			return false;
		}
	}

	/*
	** The next request is implementations of Contract Trade API.
	** Once login is invoked, user already subscribed to the following channels:
	** Contract Trade Records, Contract User Info, Contract User Position Info
	 */
	public boolean login() {
		try {
			// generate sign
			Form form = new Form();
			form.param("api_key", apiKey);
			SignForm signForm = new SignForm();
			String sign = signForm.sign(form, secretKey);

			// create request
			HashMap<String, String> parameters = new HashMap<>();
			parameters.put("api_key", apiKey);
			parameters.put("sign", sign);

			HashMap<String, Object> params = new HashMap<>();
			params.put("event", Event.login.name());
			params.put("parameters", parameters);
			String json = objectMapper.writeValueAsString(params);
			getSession().getBasicRemote().sendText(json);
			LoginResponse response = waitFor(LoginResponse.class);

			if (!response.getData().isResult()) {
				return false;
			}
			return true;
		} catch (JsonProcessingException ex) {
			log.fatal("Error converting to JSON", ex);
			return false;
		} catch (IOException ex) {
			log.fatal("Error sending request to OKEx", ex);
			return false;
		}
	}

	private Session getSession() {
		if (session == null) {
			connect();
		}
		return session;
	}

	private void connect() {
		try {
			final ClientEndpointConfig cec = ClientEndpointConfig.Builder.create()
					.decoders(Arrays.asList(PongDecoder.class,
							SubscribeContractMarketPriceInitialResponseDecoder.class,
							SubscribeContractMarketPriceResponseDecoder.class,
							SubscribeContractCandlestickChartDataInitialResponseDecoder.class,
							SubscribeContractCandlestickChartDataResponseDecoder.class,
							SubscribeContractMarketDepthIncrementalInitialResponseDecoder.class,
							SubscribeContractMarketDepthIncrementalResponseDecoder.class,
							SubscribeContractMarketDepthFullInitialResponseDecoder.class,
							SubscribeContractMarketDepthFullResponseDecoder.class,
							SubscribeContractTradeRecordInitialResponseDecoder.class,
							SubscribeContractTradeRecordResponseDecoder.class,
							SubscribeContractIndexPriceInitialResponseDecoder.class,
							SubscribeContractIndexPriceResponseDecoder.class,
							SubscribeEstimatedFuturesDeliveryPriceInitialResponseDecoder.class,
							SubscribeEstimatedFuturesDeliveryPriceSecondaryResponseDecoder.class,
							SubscribeEstimatedFuturesDeliveryPriceResponseDecoder.class,
							LoginResponseDecoder.class,
							SubscribeContractTradeRecordsResponseDecoder.class,
							SubscribeContractUserInfoFixedMarginResponseDecoder.class,
							SubscribeContractUserPositionsFixedMarginResponseDecoder.class,
							UnsubscribeResponseDecoder.class,
							StringMessageDecoder.class))
					.build();

			ClientManager client = ClientManager.createClient();
			client.getProperties().put(ClientProperties.RECONNECT_HANDLER,
					new ReconnectHandlerImpl(5));
			log.info("Connecting to server");
			client.connectToServer(this, cec,
					new URI("wss://real.okex.com:10440/websocket/okexapi"));
		} catch (URISyntaxException | DeploymentException | IOException ex) {
			log.fatal("Could not connect to OKEx", ex);
		}
	}

	private void cancelTimer() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

	private Timer getTimer() {
		if (timer == null) {
			timer = new Timer();
		}
		return timer;
	}

	@Override
	public void close() throws Exception {
		if (session != null) {
			log.info("Closing OKEx Client");
			timer.cancel();
			session.close();
		}
		okexRestClient.close();
	}

	private void invalidateSession() {
		log.info("Invalidate Session");
		session = null;
	}

	@Override
	public void onOpen(Session session, EndpointConfig config) {
		log.info("onOpen");
		this.session = session;
		session.addMessageHandler((MessageHandler.Whole<Message>) (Message t) -> handleMessage(t));

		cancelTimer();
		getTimer().schedule(new PingTask(), 1000, HEART_BEAT_INTERVAL);

		if (reconnecting) {
			reconnecting = false;
			singleThreadExecutor.submit(() -> client.onConnectionRestored());
		}
	}

	@Override
	public void onError(Session session, Throwable thr) {
		log.fatal("An error was encountered", thr);
	}

	@Override
	public void onClose(Session session, CloseReason closeReason) {
		log.info("onClose");
		invalidateSession();
	}

	private void handleMessage(Message t) {
		if (t instanceof Pong) {
			setPong((Pong) t);
		} else if (t instanceof SubscribeContractMarketPriceInitialResponse) {
			setSubscribeContractMarketPriceInitialResponse(
					(SubscribeContractMarketPriceInitialResponse) t);
		} else if (t instanceof SubscribeContractMarketPriceResponse) {
			setSubscribeContractMarketPriceResponse(
					(SubscribeContractMarketPriceResponse) t);
		} else if (t instanceof SubscribeContractCandlestickChartDataInitialResponse) {
			setSubscribeContractCandlestickChartDataInitialResponse(
					(SubscribeContractCandlestickChartDataInitialResponse) t);
		} else if (t instanceof SubscribeContractCandlestickChartDataResponse) {
			setSubscribeContractCandlestickChartDataResponse(
					(SubscribeContractCandlestickChartDataResponse) t);
		} else if (t instanceof SubscribeContractMarketDepthInitialResponse) {
			setSubscribeContractMarketDepthInitialResponse(
					(SubscribeContractMarketDepthInitialResponse) t);
		} else if (t instanceof SubscribeContractMarketDepthResponse) {
			setSubscribeContractMarketDepthResponse(
					(SubscribeContractMarketDepthResponse) t);
		} else if (t instanceof SubscribeContractTradeRecordInitialResponse) {
			setSubscribeContractTradeRecordInitialResponse(
					(SubscribeContractTradeRecordInitialResponse) t);
		} else if (t instanceof SubscribeContractTradeRecordResponse) {
			setSubscribeContractTradeRecordResponse(
					(SubscribeContractTradeRecordResponse) t);
		} else if (t instanceof SubscribeContractIndexPriceInitialResponse) {
			setSubscribeContractIndexPriceInitialResponse(
					(SubscribeContractIndexPriceInitialResponse) t);
		} else if (t instanceof SubscribeContractIndexPriceResponse) {
			setSubscribeContractIndexPriceResponse(
					(SubscribeContractIndexPriceResponse) t);
		} else if (t instanceof SubscribeEstimatedFuturesDeliveryPriceInitialResponse) {
			setSubscribeEstimatedFuturesDeliveryPriceInitialResponse(
					(SubscribeEstimatedFuturesDeliveryPriceInitialResponse) t);
		} else if (t instanceof SubscribeEstimatedFuturesDeliveryPriceSecondaryResponse) {
			setSubscribeEstimatedFuturesDeliveryPriceSecondaryResponse(
					(SubscribeEstimatedFuturesDeliveryPriceSecondaryResponse) t);
		} else if (t instanceof SubscribeEstimatedFuturesDeliveryPriceResponse) {
			setSubscribeEstimatedFuturesDeliveryPriceResponse(
					(SubscribeEstimatedFuturesDeliveryPriceResponse) t);
		} else if (t instanceof LoginResponse) {
			setLoginResponse((LoginResponse) t);
		} else if (t instanceof SubscribeContractTradeRecordsResponse) {
			setSubscribeContractTradeRecordsResponse(
					(SubscribeContractTradeRecordsResponse) t);
		} else if (t instanceof SubscribeContractsUserInfoFixedMarginResponse) {
			setSubscribeContractsUserInfoFixedMarginResponse(
					(SubscribeContractsUserInfoFixedMarginResponse) t);
		} else if (t instanceof SubscribeContractUserPositionsFixedMarginResponse) {
			setSubscribeContractUserPositionsFixedMarginResponse(
					(SubscribeContractUserPositionsFixedMarginResponse) t);
		} else if (t instanceof UnsubscribeResponse) {
			setUnsubscribeResponse((UnsubscribeResponse) t);
		} else {
			// fall back decoder. Print no decoder available + message 
			log.warn("No decoder available. Response is : \n" + t);
		}
	}

	public static interface MarketPriceListener {

		public void onMarketPrice(String symbol, Expiration expiration, MarketPrice marketPrice);
	}

	public PositionRequestResponse fetchPosition(PositionRequest positionRequest) {
		return okexRestClient.fetchPosition(positionRequest);
	}

	public OrderInfoResponse fetchOrdersInfo(OrderInfoRequest orderInfoRequest) {
		return okexRestClient.fetchOrdersInfo(orderInfoRequest);
	}

	public UserInfoResponse fetchUserInfo() {
		return okexRestClient.fetchUserInfo();
	}

	public static interface CandlestickChartDataListener {

		public void onCandlestickChartData(CandlestickChartData candlestickChartData);
	}

	public static interface MarketDepthListener {

		public void onMarketDepth(String symbol, Expiration expiration, MarketDepths marketDepths);
	}

	public static interface TradeRecordListener {

		public void onTradeRecord(String symbol, Expiration expiration, Trade tradeRecord);
	}

	public static interface IndexPriceListener {

		public void onIndexPrice(IndexPrice indexPrice);
	}

	public static interface ForecastPriceListener {

		public void onForecastPrice(ForecastPrice forecastPrice);
	}

	public static interface OrderListener {

		public void onOrder(Order order);
	}

	public static interface PositionsFixedMarginListener {

		public void onPositionsFixedMargin(PositionsFixedMargin positions);
	}

	public static interface ContractsFixedMarginListener {

		public void onContractsFixedMargin(Contracts contracts);
	}

	public static interface ConnectionListener {

		public void onConnectionLost(ClosedConnectionType closeConnectionType, String message);

		public void onConnectionRestored();
	}

	private class PingTask extends TimerTask {

		public PingTask() {
		}

		@Override
		public void run() {
			try {
				getSession().getBasicRemote().sendText("{'event':'ping'}");
				log.info("pong: " + waitFor(Pong.class));
			} catch (IOException ex) {
				log.fatal(
						"Could not send ping request", ex);
			}
		}
	}

	/**
	 * @return the pong
	 */
	private <T> T waitFor(Class<T> msgClass) {
		wLock.lock();
		try {
			if (msgClass.equals(Pong.class)) {
				newPong.await();
				return (T) pong;
			} else if (msgClass.equals(SubscribeContractMarketPriceInitialResponse.class)) {
				newSubscribeContractMarketPriceInitialResponse.await();
				return (T) subscribeContractMarketPriceInitialResponse;
			} else if (msgClass.equals(SubscribeContractCandlestickChartDataInitialResponse.class)) {
				newSubscribeContractCandlestickChartDataInitialResponse.await();
				return (T) subscribeContractCandlestickChartDataInitialResponse;
			} else if (msgClass.equals(SubscribeContractMarketDepthInitialResponse.class)) {
				newSubscribeContractMarketDepthInitialResponse.await();
				return (T) subscribeContractMarketDepthInitialResponse;
			} else if (msgClass.equals(SubscribeContractTradeRecordInitialResponse.class)) {
				newSubscribeContractTradeRecordInitialResponse.await();
				return (T) subscribeContractTradeRecordInitialResponse;
			} else if (msgClass.equals(SubscribeContractIndexPriceInitialResponse.class)) {
				newSubscribeContractIndexPriceInitialResponse.await();
				return (T) subscribeContractIndexPriceInitialResponse;
			} else if (msgClass.equals(SubscribeEstimatedFuturesDeliveryPriceInitialResponse.class)) {
				newSubscribeEstimatedFuturesDeliveryPriceInitialResponse.await();
				return (T) subscribeEstimatedFuturesDeliveryPriceInitialResponse;
			} else if (msgClass.equals(LoginResponse.class)) {
				newLoginResponse.await();
				return (T) loginResponse;
			} else if (msgClass.equals(UnsubscribeResponse.class)) {
				newUnsubscribeResponse.await();
				return (T) unsubscribeResponse;
			} else {
				throw new UnsupportedOperationException(msgClass.getSimpleName() + " is not supported");
			}
		} catch (InterruptedException ex) {
			log.fatal("Interrupted while waiting for message: " + msgClass.getSimpleName(), ex);
			return null;
		} finally {
			wLock.unlock();
		}
	}

	/**
	 * @param pong the pong to set
	 */
	private void setPong(Pong pong) {
		wLock.lock();
		try {
			this.pong = pong;
			newPong.signal();
		} finally {
			wLock.unlock();
		}
	}

	/**
	 * @param subscribeContractMarketPriceInitialResponse the
	 * subscribeContractMarketPriceInitialResponse to set
	 */
	private void setSubscribeContractMarketPriceInitialResponse(
			SubscribeContractMarketPriceInitialResponse subscribeContractMarketPriceInitialResponse) {
		wLock.lock();
		try {
			this.subscribeContractMarketPriceInitialResponse
					= subscribeContractMarketPriceInitialResponse;
			newSubscribeContractMarketPriceInitialResponse.signal();
		} finally {
			wLock.unlock();
		}
	}

	/**
	 * @param subscribeContractMarketPriceResponse the subscribeContractMarketPriceResponse to set
	 */
	private void setSubscribeContractMarketPriceResponse(
			SubscribeContractMarketPriceResponse subscribeContractMarketPriceResponse) {
		String channel = subscribeContractMarketPriceResponse.getChannel();
		//Example of channel: ok_sub_futureusd_btc_ticker_this_week
		Matcher m = CONTRACT_MARKET_PRICE_CHANNEL.matcher(channel);
		if (!m.matches()) {
			throw new RuntimeException("Channel pattern has changed from OKEX API");
		}

		String symbol = m.group(1);
		String expiration = m.group(2);
		MarketPrice marketPrice = subscribeContractMarketPriceResponse.getData();
		client.onMarketPrice(symbol, Expiration.valueOf(expiration), marketPrice);
	}

	private void setSubscribeContractCandlestickChartDataInitialResponse(
			SubscribeContractCandlestickChartDataInitialResponse subscribeContractCandlestickChartDataInitialResponse) {
		wLock.lock();
		try {
			this.subscribeContractCandlestickChartDataInitialResponse
					= subscribeContractCandlestickChartDataInitialResponse;
			newSubscribeContractCandlestickChartDataInitialResponse.signal();
		} finally {
			wLock.unlock();
		}
	}

	private void setSubscribeContractCandlestickChartDataResponse(
			SubscribeContractCandlestickChartDataResponse subscribeContractCandlestickChartDataResponse) {
		CandlestickChartData candlestickChartData;

		List<CandlestickChartData> candlestickChartDatas
				= subscribeContractCandlestickChartDataResponse.getCandlestickChartDatas();
		for (CandlestickChartData ccd : candlestickChartDatas) {
			candlestickChartData = ccd;
			client.onCandlestickChartData(candlestickChartData);
		}
	}

	private void setSubscribeContractMarketDepthInitialResponse(
			SubscribeContractMarketDepthInitialResponse subscribeContractMarketDepthInitialResponse) {
		wLock.lock();
		try {
			this.subscribeContractMarketDepthInitialResponse
					= subscribeContractMarketDepthInitialResponse;
			newSubscribeContractMarketDepthInitialResponse.signal();
		} finally {
			wLock.unlock();
		}
	}

	private void setSubscribeContractMarketDepthResponse(
			SubscribeContractMarketDepthResponse subscribeContractMarketDepthResponse) {
		String channel = subscribeContractMarketDepthResponse.getChannel();
		log.debug("channel: " + channel);
		//Example of channel: ok_sub_futureusd_btc_depth_this_week_20
		Matcher m = MARKET_DEPTH_CHANNEL.matcher(channel);
		if (!m.matches()) {
			throw new RuntimeException("Channel pattern has changed from OKEX API");
		}
		String symbol = m.group(1);
		String expiration = m.group(2);
		MarketDepths marketDepths = subscribeContractMarketDepthResponse.getData();

		client.onMarketDepth(symbol, Expiration.valueOf(expiration), marketDepths);
	}

	// subscribe to trade
	private void setSubscribeContractTradeRecordInitialResponse(
			SubscribeContractTradeRecordInitialResponse subscribeContractTradeRecordInitialResponse) {
		wLock.lock();
		try {
			this.subscribeContractTradeRecordInitialResponse
					= subscribeContractTradeRecordInitialResponse;
			newSubscribeContractTradeRecordInitialResponse.signal();
		} finally {
			wLock.unlock();
		}
	}

	// subscribe to trade
	private void setSubscribeContractTradeRecordResponse(
			SubscribeContractTradeRecordResponse subscribeContractTradeRecordResponse) {
		String channel = subscribeContractTradeRecordResponse.getChannel();
		//Example of channel: ok_sub_futureusd_btc_trade_this_week
		Matcher m = CONTRACT_TRADE_RECORD_CHANNEL.matcher(channel);
		if (!m.matches()) {
			throw new RuntimeException("Channel pattern has changed from OKEX API");
		}
		String symbol = m.group(1);
		String expiration = m.group(2);
		List<Trade> tradeRecords
				= subscribeContractTradeRecordResponse.getTradeRecords();
		for (Trade tradeRecord : tradeRecords) {
			client.onTradeRecord(symbol, Expiration.valueOf(expiration), tradeRecord);
		}
	}

	private void setSubscribeContractIndexPriceInitialResponse(
			SubscribeContractIndexPriceInitialResponse subscribeContractIndexPriceInitialResponse) {
		wLock.lock();
		try {
			this.subscribeContractIndexPriceInitialResponse
					= subscribeContractIndexPriceInitialResponse;
			newSubscribeContractIndexPriceInitialResponse.signal();
		} finally {
			wLock.unlock();
		}
	}

	private void setSubscribeContractIndexPriceResponse(
			SubscribeContractIndexPriceResponse subscribeContractIndexPriceResponse) {
		IndexPrice indexPrice = subscribeContractIndexPriceResponse.getData();
		client.onIndexPrice(indexPrice);
	}

	private void setSubscribeEstimatedFuturesDeliveryPriceInitialResponse(
			SubscribeEstimatedFuturesDeliveryPriceInitialResponse subscribeEstimatedFuturesDeliveryPriceInitialResponse) {
		wLock.lock();
		try {
			this.subscribeEstimatedFuturesDeliveryPriceInitialResponse
					= subscribeEstimatedFuturesDeliveryPriceInitialResponse;
			newSubscribeEstimatedFuturesDeliveryPriceInitialResponse.signal();
		} finally {
			wLock.unlock();
		}

	}

	private void setSubscribeEstimatedFuturesDeliveryPriceSecondaryResponse(
			SubscribeEstimatedFuturesDeliveryPriceSecondaryResponse subscribeEstimatedFuturesDeliveryPriceSecondaryResponse) {
		ForecastPrice forecastPrice
				= subscribeEstimatedFuturesDeliveryPriceSecondaryResponse.getForecastPrice();
		client.onForecastPrice(forecastPrice);
	}

	private void setSubscribeEstimatedFuturesDeliveryPriceResponse(
			SubscribeEstimatedFuturesDeliveryPriceResponse subscribeEstimatedFuturesDeliveryPriceResponse) {
		ForecastPrice forecastPrice
				= subscribeEstimatedFuturesDeliveryPriceResponse.getData().getForecastPrice();
		client.onForecastPrice(forecastPrice);
	}

	private void setLoginResponse(LoginResponse loginResponse) {
		wLock.lock();
		try {
			this.loginResponse = loginResponse;
			log.debug(loginResponse);
			if (loginResponse.getData().isResult()) {
				log.info("Login Successful...");
				newLoginResponse.signal();
			} else {
				log.debug("Login Unsuccessful...");
			}
		} finally {
			wLock.unlock();
		}

	}

	private void setUnsubscribeResponse(
			UnsubscribeResponse unsubscribeResponse) {
		wLock.lock();
		try {
			this.unsubscribeResponse = unsubscribeResponse;
			log.debug(unsubscribeResponse);
			if (this.unsubscribeResponse.getData().isResult()) {
				log.info("Unsubscribing  Successful...");
				newUnsubscribeResponse.signal();
			} else {
				log.debug("Unsubscribing is Unsuccessful...");
			}
		} finally {
			wLock.unlock();
		}

	}

	// subscribe to orders
	private void setSubscribeContractTradeRecordsResponse(
			SubscribeContractTradeRecordsResponse subscribeContractTradeRecordsResponse) {
		Order order = subscribeContractTradeRecordsResponse.getData();
		client.onOrder(order);
	}

	// subscribe to contract user info - fixed margin
	private void setSubscribeContractsUserInfoFixedMarginResponse(
			SubscribeContractsUserInfoFixedMarginResponse subscribeContractsUserInfoFixedMarginResponse) {
		Contracts contracts = subscribeContractsUserInfoFixedMarginResponse.getData();
		client.onContractsFixedMargin(contracts);
	}

	// subscribe to contract user position info - fixed margin
	private void setSubscribeContractUserPositionsFixedMarginResponse(
			SubscribeContractUserPositionsFixedMarginResponse subscribeContractUserPositionFixedMarginResponse) {
		PositionsFixedMargin positions = subscribeContractUserPositionFixedMarginResponse.data;
		client.onPositionsFixedMargin(positions);
	}

	// REST Client Call
	public PlaceOrderResponse placeOrder(PlaceOrderRequest order) {
		order.setLeverRate(leverRate);
		try (OkexFuturesRestClient restClient = new OkexFuturesRestClient(apiKey, secretKey)) {
			return restClient.placeOrder(order);
		}
	}

	public CancelOrdersResponse cancelOrders(CancelOrdersRequest order) {
		try (OkexFuturesRestClient restClient = new OkexFuturesRestClient(apiKey, secretKey)) {
			return restClient.cancelOrders(order);
		}
	}

	public static void main(String[] args) {
		PrintoutClientImpl client = new PrintoutClientImpl();
		String apiKey = "d7086a54-4080-4a2b-bb9b-d178912b1b5f";
		String secretKey = "7F2F116B07679A1ABC5AC5B2468133BA";
		int leverRate = 10;
		try (OkexConnector connector = new OkexConnector(apiKey, secretKey, leverRate, client)) {
			client.setConnector(connector);
			connector.login();
			connector.subscribeContractMarketPrice("btc", Expiration.quarter);
			Thread.sleep(5000);
//			connector.subscribeContractMarketPrice("btc", Expiration.quarter);
//			connector.subscribeContractCandlestickChartData("btc", Expiration.this_week, Interval.min1);
//			connector.subscribeContractMarketDepthIncremental("btc", Expiration.this_week);
//			connector.subscribeContractMarketDepthFull("btc", Expiration.this_week, 20);
//			connector.subscribeContractTradeRecord("btc", Expiration.this_week);
//			connector.subscribeContractIndexPrice("btc");
//			connector.subscribeEstimatedFuturesDeliveryPrice("btc");
//			Thread.sleep(5000);
//			connector.unsubscribeContractMarketPrice("btc", Expiration.quarter);
//			connector.unsubscribeContractMarketDepthFull("btc", Expiration.this_week, 20);
//			connector.unsubscribeContractTradeRecord("btc", Expiration.this_week);

//			// place order via REST API
//			String symbol = "btc_usd";
//			Expiration expiration = Expiration.quarter;
//			double price = 6423.3;
//			int amount = 1;
//
//			PlaceOrderRequest orderRequest = PlaceOrderRequest.builder().symbol(symbol)
//					.expiration(expiration).price(0.98*price).amount(amount)
//					.type(OkexOrderType.OpenLongPosition.getValue())
//					.matchPrice(MatchPrice.No.getValue()).build();
//
//			PlaceOrderResponse response = connector.placeOrder(orderRequest);
//			if (response.isResult()) {
//				System.out.println("Successfully placed order request.Order ID : " + response.getOrderId());
//			} else {
//				System.out.println("Unable to place order request. Error Code : " + response.getErrorCode());
//			}
//			orderRequest = PlaceOrderRequest.builder().symbol(symbol)
//					.expiration(expiration).price(1.02*price).amount(amount)
//					.type(OkexOrderType.CloseLongPosition.getValue())
//					.matchPrice(MatchPrice.No.getValue()).build();
//
//			response = connector.placeOrder(orderRequest);
//			if (response.isResult()) {
//				System.out.println("Successfully placed order request.Order ID : " + response.getOrderId());
//			} else {
//				System.out.println("Unable to place order request. Error Code : " + response.getErrorCode());
//			}
//
//			Thread.sleep(5000);
			// cancel order via REST API
//			String orderIds = String.valueOf(response.getOrderId());
//			CancelOrdersRequest cancelOrdersRequest = CancelOrdersRequest.builder()
//					.symbol(symbol).expiration(expiration).orderIds(orderIds).build();
//
//
//			CancelOrdersResponse cancelResponse = connector.cancelOrders(cancelOrdersRequest);
//			if (cancelResponse.isResult()) {
//				System.out.println("Successfully cancelled order request. Order ID : " + cancelResponse.getOrderId());
//			} else {
//				System.out.println("Unable to cancel order request. Error Code : " + cancelResponse.getErrorCode());
//			}
			Thread.sleep(100000);
			connector.close();
		} catch (Exception ex) {
			log.fatal(
					"Could not disconnect session", ex
			);
		}
	}

	public enum ClosedConnectionType {
		Disconnect, ConnectionFailure;
	}

	private class ReconnectHandlerImpl extends ClientManager.ReconnectHandler {

		private final long delay;

		public ReconnectHandlerImpl(long delay) {
			this.delay = delay;
		}

		@Override
		public boolean onDisconnect(CloseReason closeReason) {
			client.onConnectionLost(ClosedConnectionType.Disconnect, closeReason.getReasonPhrase());
			if (closeReason.getCloseCode() == CloseReason.CloseCodes.NORMAL_CLOSURE) {
				log.info("Disconnect due to normal closure.  No need to reconnect.");
				return false;
			}
			log.warn("### Got Disconnected.  Reconnecting...");
			reconnecting = true;
			return true;
		}

		@Override
		public boolean onConnectFailure(Exception exception) {
			client.onConnectionLost(ClosedConnectionType.ConnectionFailure, "Network Connection Problem");
			log.warn("### Reconnecting caused by:  " + exception.getMessage());
			reconnecting = true;
			return true;
		}

		@Override
		public long getDelay() {
			return delay;
		}
	}

	private static class PrintoutClientImpl extends AbstractClient {

		private OkexConnector connector;

		public PrintoutClientImpl() {
		}

		public void setConnector(OkexConnector connector) {
			this.connector = connector;
		}

		@Override
		public void onMarketPrice(String symbol, Expiration expiration, MarketPrice marketPrice) {
			log.info(marketPrice);
		}

		@Override
		public void onCandlestickChartData(CandlestickChartData candlestickChartData) {
			log.info(candlestickChartData);
		}

		@Override
		public void onMarketDepth(String symbol, Expiration expiration, MarketDepths marketDepths) {
			log.info("\nTimestamp=" + marketDepths.timestamp
					+ "\nAskDatas=" + marketDepths.getAskDatas().toString()
					+ "\nBidDatas=" + marketDepths.getBidDatas().toString());
		}

		@Override
		public void onTradeRecord(String symbol, Expiration expiration, Trade trade) {
			log.info(trade);
		}

		@Override
		public void onIndexPrice(IndexPrice indexPrice) {
			log.info(indexPrice);
		}

		@Override
		public void onForecastPrice(ForecastPrice forecastPrice) {
			log.info(forecastPrice);
		}

		@Override
		public void onOrder(Order order) {
			log.info("*** Contract Trade Records : ORDER ***");
//				log.info(order);
		}

		@Override
		public void onPositionsFixedMargin(PositionsFixedMargin positions) {
			log.info("*** Contract User Positions - Fixed Margin ***");
//				log.info(positions);
		}

		@Override
		public void onContractsFixedMargin(Contracts contracts) {
			log.info("*** Contract User Info Fixed Margin *** ");
//				log.info(contracts);
		}

		@Override
		public void onConnectionLost(ClosedConnectionType closeConnectionType, String message) {
			log.info("Connection Lost: " + closeConnectionType + ": " + message);
		}

		@Override
		public void onConnectionRestored() {
			log.info("Connection restored");
			connector.login();
			connector.subscribeContractMarketPrice("btc", Expiration.quarter);
		}
	}
}
