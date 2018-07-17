package com.stableapps.okexbookmapadapter.bm;

import com.stableapps.okexbookmapadapter.okex.AbstractClient;
import com.stableapps.okexbookmapadapter.okex.OkexConnector;
import com.stableapps.okexbookmapadapter.okex.model.CandlestickChartData;
import com.stableapps.okexbookmapadapter.okex.model.Contracts;
import com.stableapps.okexbookmapadapter.okex.model.Expiration;
import com.stableapps.okexbookmapadapter.okex.model.ForecastPrice;
import com.stableapps.okexbookmapadapter.okex.model.IndexPrice;
import com.stableapps.okexbookmapadapter.okex.model.MarketDepths;
import com.stableapps.okexbookmapadapter.okex.model.MarketPrice;
import com.stableapps.okexbookmapadapter.okex.model.Order;
import com.stableapps.okexbookmapadapter.okex.model.PositionsFixedMargin;
import com.stableapps.okexbookmapadapter.okex.model.Trade;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import lombok.extern.log4j.Log4j;
import org.apache.log4j.Level;
import org.apache.log4j.xml.DOMConfigurator;

import velox.api.layer0.annotations.Layer0LiveModule;
import velox.api.layer0.live.ExternalLiveBaseProvider;
import velox.api.layer1.Layer1ApiAdminListener;
import velox.api.layer1.data.DisconnectionReason;
import velox.api.layer1.data.InstrumentInfo;
import velox.api.layer1.data.LoginData;
import velox.api.layer1.data.LoginFailedReason;
import velox.api.layer1.data.OrderSendParameters;
import velox.api.layer1.data.OrderUpdateParameters;
import velox.api.layer1.data.TradeInfo;
import velox.api.layer1.data.UserPasswordDemoLoginData;

/**
 * This provides real time data from OKEX.
 *
 * @author aris
 */
@Layer0LiveModule
@Log4j
public class OkexRealTimeProvider extends ExternalLiveBaseProvider {

	public static final int DEFAULT_MARKET_DEPTH_AMOUNT = 20;

	private OkexConnector connector;

	protected final HashMap<String, Instrument> aliasInstruments;
	private final HashMap<String, MarketPrice> latestPrices = new HashMap<>();

	private Thread connectionThread = null;
	private String apiKey;
	private String secretKey;
	public double priceGranularity = 0.5;
	private int leverRate;
	protected final ExecutorService singleThreadExecutor;
	protected final ScheduledExecutorService singleThreadScheduledExecutor;

	public OkexRealTimeProvider() {
		DOMConfigurator.configure(getClass().getResource("/log4j.xml"));
		aliasInstruments = new HashMap<>();
		singleThreadExecutor = Executors.newSingleThreadExecutor();
		singleThreadScheduledExecutor = Executors.newSingleThreadScheduledExecutor();
	}

	@Override
	public void subscribe(String symbol, String exchange, String type) {
		singleThreadExecutor.execute(() -> {
			synchronized (aliasInstruments) {
				Expiration expiration = Expiration.valueOf(type.toLowerCase());
				String alias = createAlias(symbol, expiration);

				log.log(Level.INFO, "Subscribing to " + alias);

				if (aliasInstruments.containsKey(alias)) {
					log.log(Level.INFO, "Already subscribed to " + alias);
					instrumentListeners.forEach(l -> l.onInstrumentAlreadySubscribed(symbol, exchange, type));
					return;
				}

				boolean isSubscribed
					= getConnector().subscribeContractMarketDepthIncremental(symbol.toLowerCase(), expiration)
					&& getConnector().subscribeContractMarketPrice(symbol.toLowerCase(), expiration)
					&& getConnector().subscribeContractTradeRecord(symbol.toLowerCase(), expiration);
				if (!isSubscribed) {
					log.log(Level.INFO, "Failed to subscribed to " + alias);
					getConnector().unsubscribeContractMarketDepthFull(symbol.toLowerCase(), expiration, DEFAULT_MARKET_DEPTH_AMOUNT);
					getConnector().unsubscribeContractMarketPrice(symbol.toLowerCase(), expiration);

					instrumentListeners.forEach(l -> l.onInstrumentNotFound(symbol, exchange, type));
					return;
				}

				double pips = priceGranularity;
				final Instrument instrument = new Instrument(alias, pips);
				aliasInstruments.put(alias, instrument);

				final InstrumentInfo instrumentInfo = new InstrumentInfo(
					symbol, exchange, type, instrument.pips, 1, "", true);

				log.log(Level.INFO, "Now subscribed to " + alias);
				instrumentListeners.forEach(l -> l.onInstrumentAdded(alias, instrumentInfo));
			}
		});
	}

	protected Object[] splitToSymbolAndExpiration(String alias) {
		int splitIdx = alias.indexOf('_');
		String symbol = alias.substring(0, splitIdx);
		Expiration expiration = Expiration.valueOf(alias.substring(splitIdx + 1).toLowerCase());
		return new Object[]{symbol, expiration};
	}

	@Override
	public void unsubscribe(String alias) {
		synchronized (aliasInstruments) {
			Object[] splits = splitToSymbolAndExpiration(alias);
			String symbol = (String) splits[0];
			Expiration expiration = (Expiration) splits[1];
			getConnector().unsubscribeContractMarketDepthFull(symbol.toLowerCase(), expiration, DEFAULT_MARKET_DEPTH_AMOUNT);
			getConnector().unsubscribeContractMarketPrice(symbol.toLowerCase(), expiration);
			getConnector().unsubscribeContractTradeRecord(symbol.toLowerCase(), expiration);

			if (aliasInstruments.remove(alias) != null) {
				instrumentListeners.forEach(l -> l.onInstrumentRemoved(alias));
			}
		}
	}

	@Override
	public String formatPrice(String alias, double price) {
		// Use default Bookmap price formatting logic for simplicity.
		// Values returned by this method will be used on price axis and in few
		// other places.
		synchronized (aliasInstruments) {
			double pips;
			pips = aliasInstruments.get(alias).pips;

			return formatPriceDefault(pips, price);
		}
	}

	@Override
	public void sendOrder(OrderSendParameters orderSendParameters) {
		// This method will not be called because this adapter does not report
		// trading capabilities
		throw new RuntimeException("Not trading capable");
	}

	@Override
	public void updateOrder(OrderUpdateParameters orderUpdateParameters) {
		// This method will not be called because this adapter does not report
		// trading capabilities
		throw new RuntimeException("Not trading capable");
	}

	@Override
	public void login(LoginData loginData) {
		UserPasswordDemoLoginData userPasswordDemoLoginData = (UserPasswordDemoLoginData) loginData;
		String[] splits = userPasswordDemoLoginData.user.split("::");
		apiKey = splits[0];
		leverRate = Integer.valueOf(splits[1]);
		priceGranularity = Double.valueOf(splits[2]);
		secretKey = userPasswordDemoLoginData.password;

		// If connection process takes a while then it's better to do it in
		// separate thread
		connectionThread = new Thread(() -> handleLogin());
		connectionThread.start();
	}

	private void handleLogin() {
		// With real connection provider would attempt establishing connection
		// here.
		log.log(Level.INFO, "Logging in to OKEX");
		boolean isValid = getConnector().login();

		if (isValid) {
			// Report succesful login
			log.log(Level.INFO, "Login to OKEX successful");
			adminListeners.forEach(Layer1ApiAdminListener::onLoginSuccessful);

		} else {
			// Report failed login
			log.log(Level.INFO, "Login to OKEX failed");
			adminListeners.forEach(l -> l.onLoginFailed(LoginFailedReason.WRONG_CREDENTIALS,
				"Please provide apiKey:leverage for user and secretKey for password"));
		}

	}

	@Override
	public String getSource() {
		// String identifying where data came from.
		// For example you can use that later in your indicator.
		return "realtime demo";
	}

	@Override
	public void close() {
		try {
			log.info("Closing connector");
			connector.close();
		} catch (Exception ex) {
			log.log(Level.ERROR, "Unable to close OKEX connector", ex);
		}
	}

	public String createAlias(String symbol, Expiration expiration) {
		return symbol.toUpperCase() + "_" + expiration.name().toUpperCase();
	}

	public MarketPrice fetchLatestMarketPrice(String symbol, Expiration expiration) {
		synchronized (latestPrices) {
			return latestPrices.get(createAlias(symbol, expiration));
		}
	}

	protected void onOrder(Order order) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	protected void onPositionsFixedMargin(PositionsFixedMargin positions) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	protected void onContractsFixedMargin(Contracts contracts) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	protected void onMarketPrice(String symbol, Expiration expiration, MarketPrice marketPrice) {
	}

	private class OkexClient extends AbstractClient {

		@Override
		public void onMarketPrice(String symbol, Expiration expiration, MarketPrice marketPrice) {
			String alias = createAlias(symbol, expiration);
			synchronized (latestPrices) {
				log.debug("MarketPrice put: " + marketPrice);
				log.debug("alias: " + alias);
				latestPrices.put(alias, marketPrice);
			}
			log.debug("New market price data received for " + alias);
			log.debug("Sell: " + (int) (Double.valueOf(marketPrice.getSell()) / priceGranularity)
				+ ", Buy: " + (int) (Double.valueOf(marketPrice.getBuy()) / priceGranularity));
			assert Double.valueOf(marketPrice.getSell()) < Double.valueOf(marketPrice.getBuy());
			dataListeners.forEach(l -> l.onDepth(alias, true,
				(int) (Double.valueOf(marketPrice.getSell()) / priceGranularity), 0));
			dataListeners.forEach(l -> l.onDepth(alias, false,
				(int) (Double.valueOf(marketPrice.getBuy()) / priceGranularity), 0));

			OkexRealTimeProvider.this.onMarketPrice(symbol, expiration, marketPrice);
		}

		@Override
		public void onCandlestickChartData(CandlestickChartData candlestickChartData) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void onMarketDepth(String symbol, Expiration expiration, MarketDepths marketDepths) {
			String alias = createAlias(symbol.toUpperCase(), expiration);
			log.debug("New market depth data received for " + alias);
			log.debug(marketDepths);
			marketDepths.getAskDatas().forEach(askData -> {
				log.log(Level.DEBUG, "Ask: " + (int) (askData.getPrice() / priceGranularity) + ", amount: "
					+ (int) askData.getContractAmount());
				dataListeners.forEach(l -> l.onDepth(alias, false,
					(int) (askData.getPrice() / priceGranularity), (int) askData.getContractAmount()));
			});
			marketDepths.getBidDatas().forEach(bidData -> {
				log.log(Level.DEBUG, "Bid: " + (int) (bidData.getPrice() / priceGranularity) + ", amount: "
					+ (int) bidData.getContractAmount());
				dataListeners.forEach(l -> l.onDepth(alias, true,
					(int) (bidData.getPrice() / priceGranularity), (int) bidData.getContractAmount()));
			});

		}

		@Override
		public void onTradeRecord(String symbol, Expiration expiration, Trade tradeRecord) {
			String alias = createAlias(symbol.toUpperCase(), expiration);
			log.debug("Trade Record Received");
			log.debug("TR Details: " + tradeRecord.getType() + ", "
				+ tradeRecord.getPrice() + ", " + tradeRecord.getAmount());
			boolean isBidAggressor = tradeRecord.getType().equals("ask") ? true : false;
			boolean isOtc = false;
			if (isBidAggressor) {
				dataListeners.forEach(l -> l.onTrade(alias, tradeRecord.getPrice() / priceGranularity,
					(int) tradeRecord.getAmount(), new TradeInfo(isOtc, isBidAggressor)));
			} else {
				dataListeners.forEach(l -> l.onTrade(alias, tradeRecord.getPrice() / priceGranularity,
					(int) tradeRecord.getAmount(), new TradeInfo(isOtc, isBidAggressor)));
			}

		}

		@Override
		public void onIndexPrice(IndexPrice indexPrice) {
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}

		@Override
		public void onForecastPrice(ForecastPrice forecastPrice) {
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}

		@Override
		public void onOrder(Order order) {
			OkexRealTimeProvider.this.onOrder(order);
		}

		@Override
		public void onPositionsFixedMargin(PositionsFixedMargin positions) {
			OkexRealTimeProvider.this.onPositionsFixedMargin(positions);
		}

		@Override
		public void onContractsFixedMargin(Contracts contracts) {
			OkexRealTimeProvider.this.onContractsFixedMargin(contracts);
		}

		@Override
		public void onConnectionLost(OkexConnector.ClosedConnectionType closedConnectionType, String message) {
			log.info("Socket lost connection: " + closedConnectionType + ", " + message);
			adminListeners.forEach(listener -> {
				switch (closedConnectionType) {
					case Disconnect:
						break;
					case ConnectionFailure:
						listener.onConnectionLost(DisconnectionReason.NO_INTERNET, message);
						break;
				}
			});
		}

		@Override
		public void onConnectionRestored() {
			adminListeners.forEach(listener -> listener.onConnectionRestored());
		}

	}

	/**
	 * @return the connector
	 */
	public OkexConnector getConnector() {
		if (connector == null) {
			connector = new OkexConnector(apiKey, secretKey, leverRate, new OkexClient());
		}
		return connector;
	}

	protected class Instrument {

		protected final String alias;
		protected final double pips;

		public Instrument(String alias, double pips) {
			this.alias = alias;
			this.pips = pips;
		}

	}

	public static void main(String[] args) throws InterruptedException {
		try (OkexRealTimeProvider provider = new OkexRealTimeProvider()) {
			String apiKey = "d7086a54-4080-4a2b-bb9b-d178912b1b5f::10::0.5";
			String secretKey = "7F2F116B07679A1ABC5AC5B2468133BA";
			provider.login(new UserPasswordDemoLoginData(apiKey, secretKey, false));
			Thread.sleep(2000);
			System.out.println("Subscribing");
			provider.subscribe("BTC", "OKEX", "THIS_WEEK");
			Thread.sleep(10000);
			System.out.println("Unsubscribing");
			provider.unsubscribe("BTC_THIS_WEEK");
			Thread.sleep(20000);
		}
	}
}
