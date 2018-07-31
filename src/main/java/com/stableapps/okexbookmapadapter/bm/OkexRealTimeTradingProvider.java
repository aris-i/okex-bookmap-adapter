/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stableapps.okexbookmapadapter.bm;

import com.stableapps.okexbookmapadapter.okex.model.CancelOrdersRequest;
import com.stableapps.okexbookmapadapter.okex.model.CancelOrdersResponse;
import com.stableapps.okexbookmapadapter.okex.model.Contracts;
import com.stableapps.okexbookmapadapter.okex.model.Expiration;
import com.stableapps.okexbookmapadapter.okex.model.MarketPrice;
import com.stableapps.okexbookmapadapter.okex.model.MatchPrice;
import com.stableapps.okexbookmapadapter.okex.model.OkexOrderStatus;
import com.stableapps.okexbookmapadapter.okex.model.OkexOrderType;
import com.stableapps.okexbookmapadapter.okex.model.Order;
import com.stableapps.okexbookmapadapter.okex.model.rest.PlaceOrderRequest;
import com.stableapps.okexbookmapadapter.okex.model.rest.PlaceOrderResponse;
import com.stableapps.okexbookmapadapter.okex.model.rest.PositionRequest;
import com.stableapps.okexbookmapadapter.okex.model.rest.PositionRequestResponse;
import com.stableapps.okexbookmapadapter.okex.model.PositionsFixedMargin;
import com.stableapps.okexbookmapadapter.okex.model.rest.Contract;
import com.stableapps.okexbookmapadapter.okex.model.rest.OrderInfo;
import com.stableapps.okexbookmapadapter.okex.model.rest.OrderInfoRequest;
import com.stableapps.okexbookmapadapter.okex.model.rest.OrderInfoResponse;
import com.stableapps.okexbookmapadapter.okex.model.rest.Position;
import com.stableapps.okexbookmapadapter.okex.model.rest.UserInfoResponse;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import lombok.extern.log4j.Log4j;
import velox.api.layer0.annotations.Layer0LiveModule;
import velox.api.layer1.data.ExecutionInfoBuilder;
import velox.api.layer1.data.Layer1ApiProviderSupportedFeatures;
import velox.api.layer1.data.OrderCancelParameters;
import velox.api.layer1.data.OrderDuration;
import velox.api.layer1.data.OrderInfoBuilder;
import velox.api.layer1.data.OrderSendParameters;
import velox.api.layer1.data.OrderStatus;
import velox.api.layer1.data.OrderType;
import velox.api.layer1.data.OrderUpdateParameters;
import velox.api.layer1.data.SimpleOrderSendParameters;
import velox.api.layer1.data.SimpleOrderSendParametersBuilder;
import velox.api.layer1.data.StatusInfo;
import velox.api.layer1.data.SystemTextMessageType;
import velox.api.layer1.data.UserPasswordDemoLoginData;

/**
 * @author aris
 */
@Layer0LiveModule
@Log4j
public class OkexRealTimeTradingProvider extends OkexRealTimeProvider {

	private enum FetchInfo {
		Contracts, Positions, Orders;
	}

	private static boolean RUN_MAIN = false;
	private static final String DEFAULT_CURRENCY = "usd";
	public static final EnumSet<OkexOrderType> LONG_ORDER_TYPES
		= EnumSet.of(OkexOrderType.OpenLongPosition, OkexOrderType.CloseShortPosition);
	public static final EnumSet<OkexOrderType> SHORT_ORDER_TYPES
		= EnumSet.of(OkexOrderType.OpenShortPosition, OkexOrderType.CloseLongPosition);

	HashMap<Long, String> okexBmIds = new HashMap<>();
	HashMap<String, Long> bmOkexIds = new HashMap<>();
	private final HashMap<String, OrderInfoBuilder> bmIdWorkingOrders = new HashMap<>();
	private final HashMap<String, OrderInfoBuilder> bmIdStopOrders = new HashMap<>();
	private final EnumSet<FetchInfo> fetchInfos = EnumSet.allOf(FetchInfo.class);

	private int volume = 0;
	private final HashMap<String, ScheduledFuture<?>> aliasSendStatusInfoFutures = new HashMap<>();
	private final HashMap<String, StatusInfo> aliasStatusInfos = new HashMap<>();

	public OkexRealTimeTradingProvider() {
		super();
	}

	@Override
	public Layer1ApiProviderSupportedFeatures getSupportedFeatures() {
		// Expanding parent supported features, reporting basic trading support
		return super.getSupportedFeatures()
			.toBuilder()
			.setTrading(true)
			.setSupportedOrderDurations(Arrays.asList(new OrderDuration[]{OrderDuration.GTC}))
			.setSupportedStopOrders(Arrays.asList(new OrderType[]{OrderType.MKT}))
			.build();
	}

	@Override
	protected void onMarketPrice(String symbol, Expiration expiration, MarketPrice marketPrice) {
		synchronized (bmIdStopOrders) {
			ArrayList<String> forRemoval = new ArrayList<>();
			String alias = createAlias(symbol, expiration);
			bmIdStopOrders.entrySet()
				.stream()
				.filter(entry -> alias.equals(entry.getValue().getInstrumentAlias()))
				.forEach(entry -> {
					String bmId = entry.getKey();
					OrderInfoBuilder stopOrder = entry.getValue();
					if (stopOrder.isBuy()) {
						if (stopOrder.getStopPrice() < Double.valueOf(marketPrice.getBuy())) {
							//Convert this to market stopOrder and submit
							synchronized (aliasStatusInfos) {
								log.info("Buy Stop Order Triggered");
								StatusInfo statusInfo = aliasStatusInfos.get(alias);
								forRemoval.add(bmId);
								stopOrder.setType(OrderType.MKT);
								OkexOrderType orderType
									= statusInfo.position < 0
										? OkexOrderType.CloseShortPosition
										: OkexOrderType.OpenLongPosition;
								sendOrder(symbol, expiration, orderType,
									stopOrder.getUnfilled(), MatchPrice.Yes,
									stopOrder.getStopPrice(), stopOrder);
							}
						}
					} else {
						if (stopOrder.getStopPrice() > Double.valueOf(marketPrice.getSell())) {
							synchronized (aliasStatusInfos) {
								log.info("Sell Stop Order Triggered");
								StatusInfo statusInfo = aliasStatusInfos.get(alias);
								forRemoval.add(bmId);
								stopOrder.setType(OrderType.MKT);
								OkexOrderType orderType
									= statusInfo.position > 0
										? OkexOrderType.CloseLongPosition
										: OkexOrderType.OpenShortPosition;
								sendOrder(symbol, expiration, orderType,
									stopOrder.getUnfilled(), MatchPrice.Yes,
									stopOrder.getStopPrice(), stopOrder);
							}
						}
					}
				});
			forRemoval.forEach(bmIdStopOrders::remove);
		}
	}

	@Override
	protected void onConnectionRestored() {
		super.onConnectionRestored();
		fetchInfos.clear();
		fetchInfos.addAll(EnumSet.allOf(FetchInfo.class));
		aliasInstruments.keySet().forEach(alias -> {
			Object[] splitToSymbolAndExpiration = splitToSymbolAndExpiration(alias);
			String symbol = (String) splitToSymbolAndExpiration[0];
			Expiration expiration = (Expiration) splitToSymbolAndExpiration[1];
			sendStatusInfo(symbol, expiration);
		});
	}

	@Override
	public void sendOrder(OrderSendParameters orderSendParameters) {
		log.info("Send Order: " + orderSendParameters);
		SimpleOrderSendParameters simpleParameters = (SimpleOrderSendParameters) orderSendParameters;
		Object[] splits = splitToSymbolAndExpiration(simpleParameters.alias);
		String symbol = (String) splits[0];
		Expiration expiration = (Expiration) splits[1];

		OrderType orderType = OrderType.getTypeFromPrices(simpleParameters.stopPrice, simpleParameters.limitPrice);
		if (RUN_MAIN) {
			orderType = OrderType.LMT;
		}
		log.info("Order Type: " + orderType);

		String bmId = UUID.randomUUID().toString();
		final OrderInfoBuilder orderInfo = new OrderInfoBuilder(
			simpleParameters.alias,
			bmId,
			simpleParameters.isBuy,
			orderType,
			simpleParameters.clientId,
			simpleParameters.doNotIncrease);

		log.info("Limit Price: " + simpleParameters.limitPrice);
		log.info("Stop Price: " + simpleParameters.stopPrice);
		orderInfo.setStopPrice(simpleParameters.stopPrice)
			.setLimitPrice(simpleParameters.limitPrice)
			.setUnfilled(simpleParameters.size)
			.setDuration(OrderDuration.GTC)
			.setStatus(OrderStatus.PENDING_SUBMIT);
		tradingListeners.forEach(l -> l.onOrderUpdated(orderInfo.build()));

		orderInfo.markAllUnchanged();

		if (orderType == OrderType.STP_LMT) {
			orderInfo.setStatus(OrderStatus.REJECTED);
			tradingListeners.forEach(l -> l.onOrderUpdated(orderInfo.build()));
			orderInfo.markAllUnchanged();

			adminListeners
				.forEach(
					l -> l.onSystemTextMessage(
						"We current don't support STP_LMT",
						SystemTextMessageType.ORDER_FAILURE
					)
				);
			return;
		}

		if (orderType == OrderType.STP) {
			trackStopOrder(orderInfo, symbol, expiration);
			return;
		}

		MatchPrice matchPrice
			= orderType == OrderType.LMT
				? MatchPrice.No
				: MatchPrice.Yes;
		log.info("MatchPrice: " + matchPrice);

		int amount = simpleParameters.size;
		double price = simpleParameters.limitPrice;
		int position = 0;
		StatusInfo statusInfo = aliasStatusInfos.get(simpleParameters.alias);
		if (statusInfo != null) {
			position = statusInfo.position;
		}
		OkexOrderType okexOrderType = determineOkexOrderType(orderType,
			simpleParameters.isBuy, position);
		log.info("OkexOrderType: " + okexOrderType);
		sendOrder(symbol, expiration, okexOrderType, amount, matchPrice, price,
			orderInfo);
	}

	private void trackStopOrder(final OrderInfoBuilder orderInfo, String symbol, Expiration expiration) {
		//Since OKEX does not support stop order.  We will have to manually track stop order.
		synchronized (bmIdStopOrders) {
			orderInfo.setStatus(OrderStatus.WORKING);
			tradingListeners.forEach(l -> l.onOrderUpdated(orderInfo.build()));
			bmIdStopOrders.put(orderInfo.getOrderId(), orderInfo);
			log.info("Notify stop order working");
			tradingListeners.forEach(l -> l.onOrderUpdated(orderInfo.build()));
			orderInfo.markAllUnchanged();
			sendStatusInfo(symbol, expiration);
		}
	}

	private void sendOrder(String symbol, Expiration expiration, OkexOrderType orderType, int amount,
		MatchPrice matchPrice, double price, final OrderInfoBuilder orderInfo) {

		singleThreadExecutor.submit(() -> {
			synchronized (bmIdWorkingOrders) {
				PlaceOrderResponse orderResponse = getConnector().placeOrder(PlaceOrderRequest
					.builder()
					.amount(amount)
					.expiration(expiration)
					.matchPrice(matchPrice.getValue())
					.price(price)
					.symbol(symbol.toLowerCase())
					.type(orderType.getValue())
					.build()
				);

				if (!orderResponse.isResult()) {
					log.info("Order Rejected: " + orderResponse.getErrorCode());
					orderInfo.setStatus(OrderStatus.REJECTED);
					tradingListeners.forEach(l -> l.onOrderUpdated(orderInfo.build()));
					orderInfo.markAllUnchanged();

					adminListeners.forEach(l -> l.onSystemTextMessage("Failed to place order with error code: "
						+ orderResponse.getErrorCode(), SystemTextMessageType.ORDER_FAILURE));
					return;
				}

				log.debug("Order Id: " + orderResponse.getOrderId());
				orderInfo.setStatus(OrderStatus.WORKING);
				tradingListeners.forEach(l -> l.onOrderUpdated(orderInfo.build()));
				orderInfo.markAllUnchanged();

				okexBmIds.put(orderResponse.getOrderId(), orderInfo.getOrderId());
				bmOkexIds.put(orderInfo.getOrderId(), orderResponse.getOrderId());
				bmIdWorkingOrders.put(orderInfo.getOrderId(), orderInfo);
			}
		});
	}

	private OkexOrderType determineOkexOrderType(OrderType orderType, boolean buy,
		int position) {
		switch (orderType) {
			case LMT:
				return buy
					? OkexOrderType.OpenLongPosition
					: OkexOrderType.OpenShortPosition;
			case MKT:
				if (position > 0) {
					return buy
						? OkexOrderType.OpenLongPosition
						: OkexOrderType.CloseLongPosition;
				} else if (position < 0) {
					return !buy
						? OkexOrderType.OpenShortPosition
						: OkexOrderType.CloseShortPosition;
				} else {
					return buy
						? OkexOrderType.OpenLongPosition
						: OkexOrderType.OpenShortPosition;
				}
			default:
				String message = "No supported order type: " + orderType;
				log.warn(message);
				throw new IllegalArgumentException(message);
		}
	}

	@Override
	public void updateOrder(OrderUpdateParameters orderUpdateParameters) {
		if (orderUpdateParameters.getClass() == OrderCancelParameters.class) {
			synchronized (bmIdWorkingOrders) {
				log.info("Cancel order with provided ID: " + orderUpdateParameters.orderId);
				OrderCancelParameters orderCancelParameters = (OrderCancelParameters) orderUpdateParameters;
				Optional<OrderInfoBuilder> wOrder = Optional.ofNullable(bmIdWorkingOrders.get(orderCancelParameters.orderId));

				if (!wOrder.isPresent()) {
					log.info("Order is not from active orders, try stop orders");
					cancelStopOrder(orderUpdateParameters.orderId);
					return;
				}

				Object[] splits = splitToSymbolAndExpiration(wOrder.get().getInstrumentAlias());
				String symbol = (String) splits[0];
				Expiration expiration = (Expiration) splits[1];

				Long okexId = bmOkexIds.get(orderCancelParameters.orderId);
				sendCancelOrder(okexId, symbol, expiration);
			}
		} else {
			log.error("Unsupported order update parameter: " + orderUpdateParameters.getClass().getSimpleName());
			adminListeners
				.forEach(
					l -> l.onSystemTextMessage(
						"Unsupported order update action",
						SystemTextMessageType.ORDER_FAILURE
					)
				);
		}
	}

	private void cancelStopOrder(String orderId) {
		synchronized (bmIdStopOrders) {
			Optional<OrderInfoBuilder> wOrder = bmIdStopOrders
				.values()
				.stream()
				.filter(o -> o.getOrderId().equals(orderId))
				.findFirst();

			if (!wOrder.isPresent()) {
				log.warn("No stop Order match: " + orderId);
				return;
			}

			OrderInfoBuilder orderInfo = bmIdStopOrders.remove(orderId);
			orderInfo.setStatus(OrderStatus.CANCELLED);
			log.info("Sending Stop Order Cancelled notification");
			tradingListeners.forEach(l -> l.onOrderUpdated((orderInfo.build())));
			orderInfo.markAllUnchanged();

			Object[] splits = splitToSymbolAndExpiration(orderInfo.getInstrumentAlias());
			String symbol = (String) splits[0];
			Expiration expiration = (Expiration) splits[1];
			sendStatusInfo(symbol, expiration);
		}
	}

	private void sendCancelOrder(Long okexId, String symbol, Expiration expiration) {
		singleThreadExecutor.submit(() -> {
			CancelOrdersResponse response = getConnector().cancelOrders(
				CancelOrdersRequest.builder()
					.orderIds(String.valueOf(okexId))
					.expiration(expiration)
					.symbol(symbol.toLowerCase())
					.build()
			);

			if (!response.isResult()) {
				adminListeners
					.forEach(
						l -> l.onSystemTextMessage(
							"Could not cancel orders",
							SystemTextMessageType.ORDER_FAILURE
						)
					);
			}
		});
	}

	@Override
	public void subscribe(String symbol, String exchange, String type) {
		super.subscribe(symbol, exchange, type);
		Expiration expiration = Expiration.valueOf(type.toLowerCase());
		sendStatusInfo(symbol, expiration);
	}

	private void sendStatusInfo(String symbol, Expiration expiration) {
		/*
		We need to fetch info via REST since OKEX socket API does not provide all the info that we
		need.  Anyway this does not happen often.
		 */
		String alias = createAlias(symbol, expiration);
		final int _volume = volume;
		ScheduledFuture<?> sendStatusInfoFuture = aliasSendStatusInfoFutures.get(alias);
		if (sendStatusInfoFuture != null && !sendStatusInfoFuture.isDone()) {
			sendStatusInfoFuture.cancel(true);
		}

		sendStatusInfoFuture = singleThreadScheduledExecutor.schedule(() -> {
			synchronized (aliasInstruments) {
				if (!aliasInstruments.containsKey(alias)) {
					log.info("We are not subscribed to this: " + alias + " No need to send status update");
					return;
				}
			}

			synchronized (aliasStatusInfos) {
				synchronized (fetchInfos) {
					log.info("Sending Status Info");
					log.info("FetchInfos: " + fetchInfos);
					log.info("symbol: " + symbol + ", expiration: " + expiration);
					StatusInfo statusInfo = aliasStatusInfos.get(alias);
					String instrPair = symbol.toLowerCase() + "_" + DEFAULT_CURRENCY;

					//Contracts ##################################################
					double realizedPnl = 0.0;
					double unrealizedPnl = 0.0;

					if (statusInfo != null) {
						realizedPnl = statusInfo.realizedPnl;
						unrealizedPnl = statusInfo.unrealizedPnl;
					}

					if (fetchInfos.contains(FetchInfo.Contracts)) {
						Optional<Contract> contract = fetchContractInfo(symbol, expiration);
						if (!contract.isPresent()) {
							log.warn("Could not get latest status info from OKEX");
							adminListeners
								.forEach(
									l -> l.onSystemTextMessage(
										"Could not get latest status info from OKEX",
										SystemTextMessageType.UNCLASSIFIED
									)
								);
							return;
						}
						MarketPrice marketPrice = fetchLatestMarketPrice(symbol, expiration);
						log.info("Market Price: " + marketPrice);

						realizedPnl = contract.get().profit * Double.valueOf(marketPrice.getSell());
						unrealizedPnl = contract.get().unprofit * Double.valueOf(marketPrice.getSell());
					}

					//Orders ##################################################
					Optional<List<OrderInfo>> fetchOrdersInfo = Optional.empty();
					if (fetchInfos.contains(FetchInfo.Orders)) {
						fetchOrdersInfo = fetchOrdersInfo(instrPair, expiration);

						if (!fetchOrdersInfo.isPresent()) {
							log.warn("Could not get latest status info from OKEX");
							adminListeners
								.forEach(
									l -> l.onSystemTextMessage(
										"Could not get latest status info from OKEX",
										SystemTextMessageType.UNCLASSIFIED
									)
								);
							return;
						}

					}

					int workingBuys = 0;
					int workingSells = 0;

					synchronized (bmIdWorkingOrders) {
						synchronized (bmIdStopOrders) {
							if (fetchOrdersInfo.isPresent()) {
								fetchOrdersInfo.get().forEach(o -> {
									Optional<String> bmId = Optional.ofNullable(okexBmIds.get(o.getOrderId()));
									if (!bmId.isPresent()) {
										trackWorkingOrder(o);
									}
								});
							}

							workingBuys
								= bmIdWorkingOrders.values()
									.stream()
									.mapToInt(o -> o.isBuy() ? 1 : 0)
									.sum()
								+ bmIdStopOrders.values()
									.stream()
									.mapToInt(o -> o.isBuy() ? 1 : 0)
									.sum();
							workingSells = bmIdWorkingOrders.size() + bmIdStopOrders.size() - workingBuys;
							log.info("Working Buys: " + workingBuys);
							log.info("Working Sells: " + workingSells);
						}
					}

					//Positions ##################################################
					int position = 0;
					double avePrice = 0;

					if (statusInfo != null) {
						position = statusInfo.position;
						avePrice = statusInfo.averagePrice;
					}

					if (fetchInfos.contains(FetchInfo.Positions)) {
						PositionRequestResponse positionRequestResponse = getConnector().fetchPosition(
							PositionRequest.builder()
								.symbol(instrPair)
								.expiration(expiration)
								.build()
						);

						if (!positionRequestResponse.result) {
							adminListeners
								.forEach(
									l -> l.onSystemTextMessage(
										"Could not get latest status info from OKEX",
										SystemTextMessageType.UNCLASSIFIED
									)
								);
							return;
						}

						if (positionRequestResponse.getHolding().isEmpty()) {
							position = 0;
							avePrice = 0;
						} else {
							//Okex always return only 1 holding
							Position p = positionRequestResponse.getHolding().get(0);
							log.info("buyAmount: " + p.buyAmount);
							log.info("sellAmount: " + p.sellAmount);
							position = p.buyAmount - p.sellAmount;
							avePrice
								= (p.buyAmount * p.buyPriceAvg + p.sellAmount * p.sellPriceAvg)
								/ (p.buyAmount + p.sellAmount);

							if (p.buyAmount != 0 && p.sellAmount != 0) {
								log.info("We have both long and short position.  We will close"
									+ " positions until we have only one direction");
								//We need to maintain only one direction according to Paul of Bookmap
								//This will result to orphaned stop orders and limit orders.
								int amountToClose = Math.min(p.buyAmount, p.sellAmount);
								/*
								SimpleOrderSendParametersBuilder(java.lang.String alias, boolean isBuy, int size, OrderDuration duration, java.lang.String clientId, double limitPrice, double stopPrice, int takeProfitOffset, int stopLossOffset, int stopLossTrailingStep, int trailingStep, boolean doNotIncrease, double sizeMultiplier) 
								 */
								SimpleOrderSendParametersBuilder closeParams = new SimpleOrderSendParametersBuilder(
									alias,
									true,
									amountToClose,
									OrderDuration.GTC,
									null,
									Double.NaN,
									Double.NaN,
									0,
									0,
									0,
									0,
									true,
									1.0
								);
								sendOrder(closeParams.build());
								closeParams.setBuy(false);
								sendOrder(closeParams.build());
							}
						}
					}

					StatusInfo newStatusInfo = new StatusInfo(
						alias,
						unrealizedPnl,
						realizedPnl,
						DEFAULT_CURRENCY,
						position,
						avePrice,
						_volume,
						workingBuys,
						workingSells
					);
					aliasStatusInfos.put(alias, newStatusInfo);

					log.info("New Status Info: " + newStatusInfo);

					tradingListeners.forEach(l -> l.onStatus(newStatusInfo));
					fetchInfos.clear();
					log.info("Done Sending Status Info");
				}
			}
		}, 1, TimeUnit.SECONDS);
		aliasSendStatusInfoFutures.put(alias, sendStatusInfoFuture);
	}

	private Optional<List<OrderInfo>> fetchOrdersInfo(String instrPair, Expiration expiration) {
		OrderInfoResponse fetchOrdersInfo = getConnector().fetchOrdersInfo(
			OrderInfoRequest.builder().symbol(instrPair).expiration(expiration).build()
		);
		if (!fetchOrdersInfo.result) {
			return Optional.empty();
		}
		return Optional.of(fetchOrdersInfo.orders);
	}

	private Optional<Contract> fetchContractInfo(String symbol, Expiration expiration) {
		UserInfoResponse userInfoResponse = getConnector().fetchUserInfo();
		if (!userInfoResponse.result) {
			log.warn("fetchUserInfo failed with error code: " + userInfoResponse.errorCode);
			return Optional.empty();
		}
		Optional<Contract> contract = userInfoResponse.info
			.getSymbolsInfo()
			.get(symbol.toLowerCase())
			.getContracts()
			.stream()
			.filter(c -> c.getContractType().equalsIgnoreCase(expiration.name()))
			.findFirst();
		return contract;
	}

	@Override
	protected void onOrder(Order order) {
		synchronized (bmIdWorkingOrders) {
			Optional<String> bmId = Optional.ofNullable(okexBmIds.get(order.getOrderId()));
			log.info("Order Received: " + order.orderId);
			log.info("Order: " + order);
			log.info("BM ID isPresent: " + bmId.isPresent());
			Object[] extracts = extractSymbolAndExpiration(order.getContractName());
			String symbol = (String) extracts[0];
			Expiration expiration = (Expiration) extracts[1];
			OrderInfoBuilder wOrder = bmIdWorkingOrders.get(bmId.orElseGet(() -> trackWorkingOrder(order)));
			switch (OkexOrderStatus.of(order.getStatus())) {
				case Cancelled:
					log.info("Order Cancelled");
					wOrder.setStatus(OrderStatus.CANCELLED);
					bmOkexIds.remove(wOrder.getOrderId());
					okexBmIds.remove(order.getOrderId());
					bmIdWorkingOrders.remove(wOrder.getOrderId());
					tradingListeners.forEach(l -> l.onOrderUpdated(wOrder.build()));
					wOrder.markAllUnchanged();
					sendStatusInfo(symbol, expiration);
					break;
				case FullyFilled:
					log.info("Order filled");
					wOrder.setAverageFillPrice(order.priceAvg);
					wOrder.setUnfilled(0);
					wOrder.setFilled((int) order.amount);
					wOrder.setStatus(OrderStatus.FILLED);
					tradingListeners.forEach(l -> l.onOrderUpdated(wOrder.build()));
					wOrder.markAllUnchanged();

					bmOkexIds.remove(wOrder.getOrderId());
					okexBmIds.remove(order.getOrderId());
					bmIdWorkingOrders.remove(wOrder.getOrderId());

					/*
			ExecutionInfoBuilder(java.lang.String orderId, int size, double price, java.lang.String executionId, long time, boolean isSimulated)
					 */
					String execId = UUID.randomUUID().toString();
					ExecutionInfoBuilder executionInfoBuilder = new ExecutionInfoBuilder(
						wOrder.getOrderId(),
						wOrder.getFilled(),
						wOrder.getAverageFillPrice(),
						execId,
						System.currentTimeMillis(),
						false
					);
					tradingListeners.forEach(l -> l.onOrderExecuted(executionInfoBuilder.build()));

					volume += order.amount;
					synchronized (fetchInfos) {
						fetchInfos.addAll(EnumSet.of(FetchInfo.Positions, FetchInfo.Contracts));
					}
					sendStatusInfo(symbol, expiration);
					break;
				case CancelRequestInProcess:
					log.info("Order Cancel In Process");
					wOrder.setStatus(OrderStatus.PENDING_CANCEL);
					sendStatusInfo(symbol, expiration);
					tradingListeners.forEach(l -> l.onOrderUpdated(wOrder.build()));
					wOrder.markAllUnchanged();
					break;
				case PartiallyFilled:
				case Unfilled:
					log.info("Order Working");
					wOrder.setStatus(OrderStatus.WORKING);
					sendStatusInfo(symbol, expiration);
					tradingListeners.forEach(l -> l.onOrderUpdated(wOrder.build()));
					wOrder.markAllUnchanged();
					break;
			}
		}
	}

	@Override
	protected void onPositionsFixedMargin(PositionsFixedMargin positions) {
		positions.getPositions()
			.stream()
			.filter(p -> p.holdAmount > 0)
			.forEach(p -> {
				Object[] extracts = extractSymbolAndExpiration(p.getContractName());
				String symbol = (String) extracts[0];
				Expiration expiration = (Expiration) extracts[1];
				synchronized (fetchInfos) {
					log.info("On positions fixed margin");
					fetchInfos.addAll(EnumSet.of(FetchInfo.Positions, FetchInfo.Contracts));
				}
				sendStatusInfo(symbol, expiration);
			});
	}

	@Override
	protected void onContractsFixedMargin(Contracts contracts) {
		contracts.contracts
			.forEach(c -> {
				String cId = String.valueOf(c.contractId);
				int year = Integer.valueOf(cId.substring(0, 4));
				int month = Integer.valueOf(cId.substring(4, 6));
				int day = Integer.valueOf(cId.substring(6, 8));
				LocalDate expiry = LocalDate.of(year, month, day);
				Expiration expiration = determineExpiration(expiry);
				synchronized (fetchInfos) {
					log.info("On contracts fixed margin");
					fetchInfos.addAll(EnumSet.of(FetchInfo.Contracts));
				}

				sendStatusInfo(contracts.symbol.split("_")[0], expiration);
			});
	}

	private Expiration determineExpiration(LocalDate expiry) {
		log.debug("Determine Expiration: " + expiry);
		Expiration expiration;
		log.debug("WEEKS BETWEEN: " + ChronoUnit.WEEKS.between(LocalDate.now(), expiry));
		switch ((int) ChronoUnit.WEEKS.between(LocalDate.now(), expiry)) {
			case 0:
				expiration = Expiration.this_week;
				break;
			case 1:
				expiration = Expiration.next_week;
				break;
			default:
				expiration = Expiration.quarter;
		}
		log.debug("Expiration: " + expiration);
		return expiration;
	}

	public static void main(String[] args) throws InterruptedException {
		OkexRealTimeTradingProvider.RUN_MAIN = true;
		try (OkexRealTimeTradingProvider provider = new OkexRealTimeTradingProvider()) {
			String apiKey = "d7086a54-4080-4a2b-bb9b-d178912b1b5f";
			String secretKey = "7F2F116B07679A1ABC5AC5B2468133BA";
			int leverRate = 10;
			provider.login(new UserPasswordDemoLoginData(apiKey + "::" + leverRate + "::0.5", secretKey, false));
			Thread.sleep(2000);

			provider.subscribe("BTC", "OKEX", "QUARTER");

//			double price = 6500.00;
//			SimpleOrderSendParameters params = new SimpleOrderSendParameters("BTC_QUARTER", true, 1,
//					OrderDuration.GTC, price, price, 1);
//			provider.sendOrder(params);
//
//			Thread.sleep(2000);
//			String[] orderIds = provider.bmIdWorkingOrders.keySet().toArray(new String[0]);
//			Arrays.stream(orderIds).forEach(bmId -> {
//				OrderCancelParameters cancelParams = new OrderCancelParameters(bmId);
//				provider.updateOrder(cancelParams);
//			});
			Thread.sleep(Long.MAX_VALUE);
		}
	}

	private String trackWorkingOrder(Order order) {
		synchronized (bmIdWorkingOrders) {
			Object[] extracts = extractSymbolAndExpiration(order.contractName);
			String alias = createAlias((String) extracts[0], (Expiration) extracts[1]);

			boolean isBuy = LONG_ORDER_TYPES.contains(OkexOrderType.valueOf(order.type));
			String bmId = String.valueOf(order.orderId);
			long okexId = order.orderId;
			double price = order.price;
			int amount = (int) order.amount;

			OrderInfoBuilder builder = createWorkingOrderInfoBuilder(alias, bmId, isBuy, price, amount);

			tradingListeners.forEach(l -> l.onOrderUpdated(builder.build()));
			builder.markAllUnchanged();

			okexBmIds.put(okexId, bmId);
			bmOkexIds.put(bmId, okexId);
			bmIdWorkingOrders.put(builder.getOrderId(), builder);
			return bmId;
		}
	}

	private String trackWorkingOrder(OrderInfo order) {
		synchronized (bmIdWorkingOrders) {
			Object[] extracts = extractSymbolAndExpiration(order.contractName);
			String alias = createAlias((String) extracts[0], (Expiration) extracts[1]);

			String bmId = String.valueOf(order.orderId);
			boolean isBuy = LONG_ORDER_TYPES.contains(OkexOrderType.valueOf(order.type));
			long okexId = order.orderId;
			double price = order.price;
			int amount = order.amount;

			OrderInfoBuilder builder = createWorkingOrderInfoBuilder(alias, bmId, isBuy, price, amount);

			tradingListeners.forEach(l -> l.onOrderUpdated(builder.build()));
			builder.markAllUnchanged();

			okexBmIds.put(okexId, bmId);
			bmOkexIds.put(bmId, okexId);
			bmIdWorkingOrders.put(builder.getOrderId(), builder);

			return bmId;
		}
	}

	private OrderInfoBuilder createWorkingOrderInfoBuilder(String alias, String bmId, boolean isBuy, double price, int amount) {
		final OrderInfoBuilder builder = new OrderInfoBuilder(
			alias,
			bmId,
			isBuy,
			OrderType.LMT,//Okex only supports LMT orders
			null,//Client ID can be null if coming from exchange
			false//We set this to false since we shouldn't worry about it in L0 according to ducumentation
		);
		builder.setStopPrice(0.0)
			.setLimitPrice(price)
			.setUnfilled(amount)
			.setDuration(OrderDuration.GTC)
			.setStatus(OrderStatus.WORKING);
		return builder;
	}

	private Object[] extractSymbolAndExpiration(String contractName) {
		String symbol = contractName.substring(0, 3);
		int month = Integer.valueOf(contractName.substring(3, 5));
		int day = Integer.valueOf(contractName.substring(5, 7));
		LocalDate expiry = LocalDate.of(LocalDate.now().getYear(), month, day);
		Expiration expiration = determineExpiration(expiry);
		return new Object[]{symbol, expiration};
	}
}
