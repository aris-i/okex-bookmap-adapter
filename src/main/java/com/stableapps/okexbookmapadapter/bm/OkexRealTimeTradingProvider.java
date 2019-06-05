/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stableapps.okexbookmapadapter.bm;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.tuple.Pair;
import org.java_websocket.util.Base64;

import com.stableapps.okexbookmapadapter.okex.model.Expiration;
import com.stableapps.okexbookmapadapter.okex.model.FutureAccountsContractFixedMargin;
import com.stableapps.okexbookmapadapter.okex.model.FuturesAccount;
import com.stableapps.okexbookmapadapter.okex.model.FuturesPosition;
import com.stableapps.okexbookmapadapter.okex.model.FuturesPositionsOfCurrencyResponse;
import com.stableapps.okexbookmapadapter.okex.model.OkexOrderTypeFuturesOrSwap;
import com.stableapps.okexbookmapadapter.okex.model.OrderData;
import com.stableapps.okexbookmapadapter.okex.model.OrderDataFutures;
import com.stableapps.okexbookmapadapter.okex.model.OrderDataSpot;
import com.stableapps.okexbookmapadapter.okex.model.OrdersFuturesList;
import com.stableapps.okexbookmapadapter.okex.model.SpotAccount;
import com.stableapps.okexbookmapadapter.okex.model.SubscribeFuturesAccountResponse;
import com.stableapps.okexbookmapadapter.okex.model.SubscribeFuturesPositionResponse;
import com.stableapps.okexbookmapadapter.okex.model.rest.AccountSpot;
import com.stableapps.okexbookmapadapter.okex.model.rest.AccountsFuturesCrossMargin;
import com.stableapps.okexbookmapadapter.okex.model.rest.AccountsFuturesFixedMarginResponse;
import com.stableapps.okexbookmapadapter.okex.model.rest.InstrumentFutures;
import com.stableapps.okexbookmapadapter.okex.model.rest.InstrumentGeneric;
import com.stableapps.okexbookmapadapter.okex.model.rest.InstrumentSpot;
import com.stableapps.okexbookmapadapter.okex.model.rest.PlaceOrderRequest;
import com.stableapps.okexbookmapadapter.okex.model.rest.PlaceOrderRequestFuturesOrSwap;
import com.stableapps.okexbookmapadapter.okex.model.rest.PlaceOrderRequestTokenOrMargin;
import com.stableapps.okexbookmapadapter.okex.model.rest.PlaceOrderResponse;
import com.stableapps.okexbookmapadapter.okex.rest.OkexFuturesRestClient;
import com.stableapps.okexbookmapadapter.okex.utils.OkexUtils;

import lombok.Data;
import velox.api.layer1.Layer1ApiAdminListener;
import velox.api.layer1.common.Log;
import velox.api.layer1.data.BalanceInfo;
import velox.api.layer1.data.BalanceInfo.BalanceInCurrency;
import velox.api.layer1.data.ExecutionInfoBuilder;
import velox.api.layer1.data.Layer1ApiProviderSupportedFeatures;
import velox.api.layer1.data.LoginData;
import velox.api.layer1.data.LoginFailedReason;
import velox.api.layer1.data.OrderCancelParameters;
import velox.api.layer1.data.OrderDuration;
import velox.api.layer1.data.OrderInfoBuilder;
import velox.api.layer1.data.OrderInfoUpdate;
import velox.api.layer1.data.OrderSendParameters;
import velox.api.layer1.data.OrderStatus;
import velox.api.layer1.data.OrderType;
import velox.api.layer1.data.OrderUpdateParameters;
import velox.api.layer1.data.SimpleOrderSendParameters;
import velox.api.layer1.data.SubscribeInfo;
import velox.api.layer1.data.SystemTextMessageType;
import velox.api.layer1.data.UserPasswordDemoLoginData;

/**
 * @author aris
 */

public class OkexRealTimeTradingProvider extends OkexRealTimeProvider {

	private static final String ONE_DIR_CLOSE_CLIENT_ID = "OneDirectionCloseClientID";
	private static boolean RUN_MAIN = false;
	public static final EnumSet<OkexOrderTypeFuturesOrSwap> LONG_ORDER_TYPES
		= EnumSet.of(OkexOrderTypeFuturesOrSwap.OpenLongPosition, OkexOrderTypeFuturesOrSwap.CloseShortPosition);
	public static final EnumSet<OkexOrderTypeFuturesOrSwap> SHORT_ORDER_TYPES
		= EnumSet.of(OkexOrderTypeFuturesOrSwap.OpenShortPosition, OkexOrderTypeFuturesOrSwap.CloseLongPosition);

	HashMap<Long, String> okexBmIds = new HashMap<>();
	HashMap<String, Long> bmOkexIds = new HashMap<>();
	private final HashMap<String, OrderInfoBuilder> bmIdWorkingOrders = new HashMap<>();
	private final HashMap<String, OrderInfoBuilder> bmIdSentOrders = new HashMap<>();
	private final HashMap<String, String> clientOidToOrderId= new HashMap<>();
	private final HashMap<String, String> orderIdToClientOid = new HashMap<>();
	private Map <String, Pair <Integer, Integer>> positionsMap = new HashMap<>();
	private Map <String, Set<String>> currenciesForSpotBalance = new HashMap<>();
	private Map <String, Set<String>> currenciesForSpotPosition = new HashMap<>();
	private Map<String, UnrealizedPnlData> unrealizedPnlMap = new HashMap<>();
	
	
	@Data
	private static class UnrealizedPnlData{
	    int qty;
	    double avrCost;
	    double lastBidTrade;
	    double lastAskTrade;
	}
	
	public OkexRealTimeTradingProvider() {
		super();
	}
	
	@Override
	public Layer1ApiProviderSupportedFeatures getSupportedFeatures() {
		// Expanding parent supported features, reporting basic trading support
		return super.getSupportedFeatures()
			.toBuilder()
			.setExchangeUsedForSubscription(false)
			.setBalanceSupported(true)
			.setTrading(true)
			.setSupportedStopOrders(Arrays.asList(new OrderType[] {
                    OrderType.MKT,
                    OrderType.LMT
            }))
			.setSupportedOrderDurations(Arrays.asList(new OrderDuration[]{
			        OrderDuration.GTC,
			        OrderDuration.GTC_PO,
			        OrderDuration.FOK,
			        OrderDuration.IOC,
			        }))
			.build();
	}
	
	   @Override
	    public void login(LoginData loginData) {
	        UserPasswordDemoLoginData userPasswordDemoLoginData = (UserPasswordDemoLoginData) loginData;
	        
	        if (userPasswordDemoLoginData.user.isEmpty() || userPasswordDemoLoginData.password.isEmpty()) {
	            adminListeners.forEach(l -> l.onLoginFailed(
	                    LoginFailedReason.WRONG_CREDENTIALS,
	                    "Login or/and password field is empty")
	                );
	            return;
	        }
	        
	        String[] splits = userPasswordDemoLoginData.user.split("::");
	        try {
	            assert splits.length == 2;
	            apiKey = splits[0];
	            passPhraze = splits[1];
	            secretKey = userPasswordDemoLoginData.password;
	        } catch (Exception e) {
	            Log.info("Could not login.", e);
	            adminListeners.forEach(l -> l.onLoginFailed(
	                LoginFailedReason.WRONG_CREDENTIALS,
	                INVALID_USERNAME_PASSWORD)
	            );
	            return;
	        }

	        // If connection process takes a while then it's better to do it in
	        // separate thread
	        connectionThread = new Thread(() -> handleLogin());
	        connectionThread.start();
	    }

	    private void handleLogin() {
	        Log.info("Logging in to OKEX");
	        boolean isValid = getNewConnector().wslogin();

	        if (isValid) {
	            Log.info("Login to OKEX successful");
	            adminListeners.forEach(Layer1ApiAdminListener::onLoginSuccessful);
	        } else {
	            Log.info("Login to OKEX failed");
	            adminListeners.forEach(l -> l.onLoginFailed(
	                LoginFailedReason.WRONG_CREDENTIALS,
	                INVALID_USERNAME_PASSWORD)
	            );
	        }

	    }

	@Override
	protected void onConnectionRestored() {
        Log.info("\t\tOkexClient (TradingProvider)" + this.hashCode() +  ": onConnectionRestored()");
	}

	@Override
	public void sendOrder(OrderSendParameters orderSendParameters) {
		SimpleOrderSendParameters simpleParameters = (SimpleOrderSendParameters) orderSendParameters;
		String symbol = simpleParameters.alias;
		Expiration expiration = null;

		OrderType orderType = OrderType.getTypeFromPrices(simpleParameters.stopPrice, simpleParameters.limitPrice);
		if (RUN_MAIN) {
			orderType = OrderType.LMT;
		}
		Log.info("Order Type: " + orderType);

		String bmId = UUID.randomUUID().toString();
		final OrderInfoBuilder orderInfo = new OrderInfoBuilder(
			simpleParameters.alias,
			bmId,
			simpleParameters.isBuy,
			orderType,
			simpleParameters.clientId.equals(ONE_DIR_CLOSE_CLIENT_ID)
			? null : simpleParameters.clientId,
			simpleParameters.doNotIncrease);

		Log.info("Limit Price: " + simpleParameters.limitPrice);
		Log.info("Stop Price: " + simpleParameters.stopPrice);
		orderInfo.setStopPrice(simpleParameters.stopPrice)
			.setLimitPrice(simpleParameters.limitPrice)
			.setUnfilled(simpleParameters.size)
			.setDuration(simpleParameters.duration)
			.setStatus(OrderStatus.PENDING_SUBMIT);
		tradingListeners.forEach(l -> l.onOrderUpdated(orderInfo.build()));

		orderInfo.markAllUnchanged();
		
		if (orderType == OrderType.STP 
		        || orderType == OrderType.STP_LMT 
		        || simpleParameters.stopLossOffset != 0
		        || simpleParameters.takeProfitOffset != 0
		        || simpleParameters.trailingStep > 0
		        ) {
			orderInfo.setStatus(OrderStatus.REJECTED);
			tradingListeners.forEach(l -> l.onOrderUpdated(orderInfo.build()));
			orderInfo.markAllUnchanged();

			adminListeners.forEach(l -> l.onSystemTextMessage(
						"StopLoss or TakeProfit orders are not supported at the moment",
						SystemTextMessageType.ORDER_FAILURE
					)
				);
			return;
		}

		int size = simpleParameters.size;
		double price = simpleParameters.limitPrice;
		int position = 0;
		StatusInfoLocal statusInfo = aliasedStatusInfos.get(simpleParameters.alias);
		if (statusInfo != null) {
			position = statusInfo.position;
		}
		OkexOrderTypeFuturesOrSwap okexOrderType = determineOkexOrderType(orderType,
			simpleParameters.isBuy, position, simpleParameters.clientId);
		Log.info("OkexOrderType: " + okexOrderType);
		sendOrder(symbol, expiration, okexOrderType, size, price,
			orderInfo);
	}

    private void sendOrder(String alias, Expiration expiration, OkexOrderTypeFuturesOrSwap orderType, int size,
            double price, final OrderInfoBuilder orderInfo) {

        PlaceOrderRequest orderRequest = new PlaceOrderRequest();
        String instrumentType = alias.substring(0, alias.indexOf('@'));
        String symbol = alias.substring(alias.indexOf('@') + 1);

        switch (instrumentType) {
        case "spot":
            PlaceOrderRequestTokenOrMargin spotRequest = new PlaceOrderRequestTokenOrMargin();
            spotRequest.setType("limit");
            spotRequest.setSide(orderInfo.isBuy() ? "buy" : "sell");
            spotRequest.setInstrumentId(symbol);
            spotRequest.setMarginTrading(1);//
            spotRequest.setDuration(OkexUtils.getDurationType(orderInfo.getDuration()));

            if (orderInfo.getType() == OrderType.LMT) {
                spotRequest.setPrice(price);
                spotRequest.setFloatingPointSize(size * getMinSize(alias));
            } else if (orderInfo.getType() == OrderType.MKT) {
                spotRequest.setFloatingPointSize(size * getMinSize(alias));
                int bestPrice = ((OkexClient) connector.client).getBestPrice(orderInfo.isBuy(), alias);
                spotRequest.setPrice(bestPrice);
            }

            orderRequest = spotRequest;
            break;
        case "margin":
            PlaceOrderRequestTokenOrMargin marginRequest = new PlaceOrderRequestTokenOrMargin();
            marginRequest.setType(orderInfo.getType().equals(OrderType.MKT) ? "market" : "limit");
            marginRequest.setMarginTrading(2);
            orderRequest = marginRequest;
            break;
        case "futures":
            PlaceOrderRequestFuturesOrSwap futures = new PlaceOrderRequestFuturesOrSwap();
            futures.setType(orderInfo.isBuy() ? "1" : "2");
            futures.setInstrumentId(symbol);
            futures.setOrderType(OkexUtils.getDurationType(orderInfo.getDuration()));

            if (orderInfo.getType().equals(OrderType.MKT)) {
                futures.setMatchPrice("1");
            } else {
                futures.setPrice(orderInfo.getLimitPrice());
            }
            futures.setLeverage(String.valueOf(leverRate));
            futures.setFloatingPointSize(null);
            futures.setSize(size);//
            orderRequest = futures;
            break;

        }

        String clientOid = orderInfo.getClientId();
        byte[] bytes = clientOid.getBytes();
        byte[] decBytes;
        String convertedId = "";
        try {
            decBytes = Base64.decode(bytes);
            convertedId = Hex.encodeHexString(decBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        orderRequest.setClientOrderId(convertedId);
        final String workaroundId = convertedId;
        PlaceOrderRequest workaroundRequest = orderRequest;
        
        int positionLong = 0;
        int positionShort = 0;
        boolean isCodirectionalWithPosition = true;

        if (instrumentType.equals("futures")) {
            positionLong = positionsMap.get(alias).getRight();
            positionShort = positionsMap.get(alias).getLeft();
            isCodirectionalWithPosition = positionsMap.get(alias) == null || positionShort == positionLong
                    || !orderInfo.isBuy() && positionShort > positionLong
                    || orderInfo.isBuy() && positionLong > positionShort;
        }

        if (instrumentType.equals("futures") && orderInfo.getType() == OrderType.MKT && !isCodirectionalWithPosition) {

            if (size == Math.abs(positionLong - positionShort)) {

                if (positionLong != 0) {
                    closeFuturesPosition(workaroundRequest, workaroundId, orderInfo, "3", positionLong);
                }
                if (positionShort != 0) {
                    closeFuturesPosition(workaroundRequest, workaroundId, orderInfo, "4", positionShort);
                }
            } else {
                if (orderInfo.isBuy()) {
                    closeFuturesPosition(workaroundRequest, workaroundId, orderInfo, "4", size);
                } else {
                    closeFuturesPosition(workaroundRequest, workaroundId, orderInfo, "3", size);
                }
            }
        } else {

            singleThreadExecutor.submit(() -> {
                Log.debug("+++Enter synchronize");
                synchronized (bmIdSentOrders) {
                    bmIdSentOrders.put(workaroundId, orderInfo);

                    PlaceOrderResponse orderResponse = getConnector().placeOrder(workaroundRequest);
                    
                    orderInfo.setStatus(OrderStatus.PENDING_SUBMIT);
                    tradingListeners.forEach(l -> l.onOrderUpdated(orderInfo.build()));
                    orderInfo.markAllUnchanged();

                    if (!orderResponse.isResult()) {
                        Log.info("REST Order Rejected: " + orderResponse.getErrorCode() + " "
                                + orderResponse.getErrorMessage());
                        orderInfo.setStatus(OrderStatus.REJECTED);
                        tradingListeners.forEach(l -> l.onOrderUpdated(orderInfo.build()));
                        orderInfo.markAllUnchanged();

                        adminListeners
                                .forEach(l -> l.onSystemTextMessage(
                                        "Failed to place order with error code: " + orderResponse.getErrorCode() + "\n"
                                                + orderResponse.getErrorMessage(),
                                        SystemTextMessageType.ORDER_FAILURE));
                        return;
                    }
                }
            });
        }
    }

	private OkexOrderTypeFuturesOrSwap determineOkexOrderType(OrderType orderType, boolean buy,
		int position, String clientId) {
		switch (orderType) {
			case LMT:
				return buy
					? OkexOrderTypeFuturesOrSwap.OpenLongPosition
					: OkexOrderTypeFuturesOrSwap.OpenShortPosition;
			case MKT:
				if (clientId != null && clientId.equals(ONE_DIR_CLOSE_CLIENT_ID)) {
					return buy
						? OkexOrderTypeFuturesOrSwap.CloseShortPosition
						: OkexOrderTypeFuturesOrSwap.CloseLongPosition;
				} else if (position > 0) {
					return buy
						? OkexOrderTypeFuturesOrSwap.OpenLongPosition
						: OkexOrderTypeFuturesOrSwap.CloseLongPosition;
				} else if (position < 0) {
					return !buy
						? OkexOrderTypeFuturesOrSwap.OpenShortPosition
						: OkexOrderTypeFuturesOrSwap.CloseShortPosition;
				} else {
					return buy
						? OkexOrderTypeFuturesOrSwap.OpenLongPosition
						: OkexOrderTypeFuturesOrSwap.OpenShortPosition;
				}
			default:
				String message = "No supported order type: " + orderType;
				Log.info(message);
				throw new IllegalArgumentException(message);
		}
	}

	@Override
	public void updateOrder(OrderUpdateParameters orderUpdateParameters) {
		if (orderUpdateParameters.getClass() == OrderCancelParameters.class) {
			Log.debug("+++Enter synchronize");
			synchronized (bmIdWorkingOrders) {
				Log.info("Cancel order with provided ID: " + orderUpdateParameters.orderId);
				OrderCancelParameters orderCancelParameters = (OrderCancelParameters) orderUpdateParameters;
				
				String id = orderIdToClientOid.get(orderCancelParameters.orderId);
				
				String alias = bmIdWorkingOrders.get(id).getInstrumentAlias();
	            int at = alias.indexOf('@');
	            String symbol = alias.substring(at + 1);
	            String type = alias.substring(0, at);
		        String body = "{\"client_oid\":\"" + id + "\", \"instrument_id\":\"" + symbol + "\"}";
				String path = "";
				
				if (type.equals("spot")) {
				    path = "/api/spot/v3/cancel_orders/";
				} else if (type.equals("futures")) {
                    path = "/api/futures/v3/cancel_order/" + symbol + "/";
                }
				connector.okexRestClient.call(path + id, body, String.class, apiKey, secretKey, connector.passPhraze);
			}
		} else {
            Log.error("Unsupported order update parameter: " + orderUpdateParameters.getClass().getSimpleName());
            adminListeners.forEach(
                    l -> l.onSystemTextMessage("Unsupported order update action", SystemTextMessageType.ORDER_FAILURE));
        }
    }

    @SuppressWarnings("unlikely-arg-type")
    protected void onOrder(OrderData order) {

        synchronized (bmIdWorkingOrders) {
            Optional<String> bmId = Optional.ofNullable(okexBmIds.get(order.getOrderId()));
            Log.info("Order Received: " + order.getOrderId());
            Log.info("Order: " + order);
            Log.info("BM ID isPresent: " + bmId.isPresent());
            String alias = order.getInstrumentType() + "@" + order.getInstrumentId();

            boolean isBuy = false;
            if (order instanceof OrderDataSpot) {
                isBuy = ((OrderDataSpot) order).getSide().equals("buy");
                order.setInstrumentType("spot");
            } else if (order instanceof OrderDataFutures) {
                order.setInstrumentType("futures");
                OrderDataFutures futures = (OrderDataFutures) order;

                if (futures.getType().equals("1") || futures.getType().equals("4")) {
                    isBuy = true;
                } else if (futures.getType().equals("2") || futures.getType().equals("3")) {
                    isBuy = false;
                } else {
                    throw new RuntimeException("futures order type = " + futures.getType());
                }
            } else {
                throw new RuntimeException("unknown order type");
            }

            if (order.getClientOid().equals("") || !bmIdSentOrders.containsKey(order.getClientOid())) {
                

                OrderInfoBuilder newBuilder = new OrderInfoBuilder(alias, order.getOrderId(), isBuy,
                        order.getType().equals("limit") ? OrderType.LMT : OrderType.MKT, "", false);
                if (order instanceof OrderDataSpot && order.getType().equals("limit")
                        || order instanceof OrderDataFutures
                                && ((order.getType().equals("1") || order.getType().equals("4"))
                                        || (order.getType().equals("2") || order.getType().equals("3")))) {
                    newBuilder.setLimitPrice(order.getPrice());
                    newBuilder.setType(OrderType.LMT);
                }

                order.setClientOid(order.getOrderId());

                if (order instanceof OrderDataSpot) {
                    newBuilder.setUnfilled((int) Math.round(order.getSize() / OkexRealTimeProvider.getMinSize(alias)));
                }
                if (order instanceof OrderDataFutures) {
                    newBuilder
                            .setUnfilled((int) Math.round(order.getSize() - ((OrderDataFutures) order).getFilledQty()));
                }

                newBuilder.setDuration(OrderDuration.GTC);

                OrderStatus status;
                if (order.getState().equals(0)) {
                    status = OrderStatus.PENDING_SUBMIT;
                    bmIdSentOrders.put(order.getOrderId(), newBuilder);
                } else {
                    status = OrderStatus.WORKING;
                    bmIdWorkingOrders.put(order.getOrderId(), newBuilder);
                }

                newBuilder.setStatus(status);
                tradingListeners.forEach(l -> l.onOrderUpdated(newBuilder.build()));
            }

            OrderInfoBuilder orderInfo;
            OrderInfoUpdate update;

            StatusInfoLocal info = aliasedStatusInfos.computeIfAbsent(alias, v -> {
                StatusInfoLocal infoLocal =  new StatusInfoLocal();
                infoLocal.setInstrumentAlias(alias);
                return infoLocal;
                });
            int buyOpenOrders = info.getWorkingBuys();
            int sellOpenOrders = info.getWorkingSells();
            
            switch (order.getState()) {
            case -2:// previous API "rejected" current API "failed"
                orderInfo = bmIdSentOrders.get(order.getClientOid());
                bmIdSentOrders.remove(orderInfo.getClientId());

                orderInfo.setStatus(OrderStatus.REJECTED);
                update = orderInfo.build();
                tradingListeners.forEach(l -> l.onOrderUpdated(update));
                orderInfo.markAllUnchanged();
                break;
            case 0: // "open"
                orderInfo = bmIdSentOrders.get(order.getClientOid());
                bmIdSentOrders.remove(orderInfo.getClientId());

                Log.info("ORDER OPEN clOid" + order.getClientOid() + " clId " + orderInfo.getClientId());
                orderInfo.setStatus(OrderStatus.WORKING);
                update = orderInfo.build();
                tradingListeners.forEach(l -> l.onOrderUpdated(update));
                orderInfo.markAllUnchanged();

                clientOidToOrderId.put(order.getClientOid(), orderInfo.getOrderId());
                orderIdToClientOid.put(orderInfo.getOrderId(), order.getClientOid());
                bmIdWorkingOrders.put(order.getClientOid(), orderInfo);
                
                if (isBuy) {
                    info.setWorkingBuys(++buyOpenOrders);
                } else {
                    info.setWorkingSells(++sellOpenOrders);
                }
                break;
            case -1:// "cancelled"
                orderInfo = bmIdWorkingOrders.get(order.getClientOid());
                Log.info("Order Cancelled");
                orderInfo.setStatus(OrderStatus.CANCELLED);
                bmOkexIds.remove(orderInfo.getOrderId());
                okexBmIds.remove(order.getOrderId());
                bmIdWorkingOrders.remove(orderInfo.getOrderId());
                update = orderInfo.build();
                tradingListeners.forEach(l -> l.onOrderUpdated(update));
                orderInfo.markAllUnchanged();
                
                if (isBuy) {
                    info.setWorkingBuys(--buyOpenOrders);
                } else {
                    info.setWorkingSells(--sellOpenOrders);
                }
                break;
            case 2:// "fully filled"
                orderInfo = bmIdWorkingOrders.get(order.getClientOid());
                Log.info("Order filled");
                orderInfo.setUnfilled(0);
                if (order.getInstrumentType().equals("spot")) {
                    orderInfo.setFilled((int) Math.round(order.getSize()/getMinSize(alias)));
                } else {
                    orderInfo.setFilled((int) order.getSize());// !!!!!!!!!!! size is fp, turn to integer
                }
                
                orderInfo.setStatus(OrderStatus.FILLED);
                orderInfo.setAverageFillPrice(order.getLastFillPx());
                update = orderInfo.build();
                tradingListeners.forEach(l -> l.onOrderUpdated(update));
                orderInfo.markAllUnchanged();
                bmOkexIds.remove(orderInfo.getOrderId());
                okexBmIds.remove(order.getOrderId());
                bmIdWorkingOrders.remove(orderInfo.getOrderId());
                String execId = UUID.randomUUID().toString();
                ExecutionInfoBuilder executionInfoBuilder = new ExecutionInfoBuilder(orderInfo.getOrderId(),
                        orderInfo.getFilled(), orderInfo.getAverageFillPrice(), execId, System.currentTimeMillis(),
                        false);
                tradingListeners.forEach(l -> l.onOrderExecuted(executionInfoBuilder.build()));

                if (isBuy) {
                    info.setWorkingBuys(--buyOpenOrders);
                } else {
                    info.setWorkingSells(--sellOpenOrders);
                }
                break;
            case 4:// cancel in process
                Log.info("Order Cancel In Process");
                orderInfo = bmIdWorkingOrders.get(order.getClientOid());
                orderInfo.setStatus(OrderStatus.PENDING_CANCEL);
                tradingListeners.forEach(l -> l.onOrderUpdated(orderInfo.build()));
                orderInfo.markAllUnchanged();
                break;
//				case PartiallyFilled: not implemented
//				case Unfilled: not implemented
            }
            updateStatus(info);
        }
    }
   
    protected void onFuturesAccount(SubscribeFuturesAccountResponse response) {
	    
        Map<String, FuturesAccount> map = response.data.get(0);

        for (String token : map.keySet()) {
            FuturesAccount account = map.get(token);

            if (account.getContracts() != null) {
                List<FutureAccountsContractFixedMargin> contracts = account.getContracts();

                for (FutureAccountsContractFixedMargin contract : contracts) {
                    account.setRealizedPnl(account.getRealizedPnl() + contract.getRealizedPnl());
                    account.setUnrealizedPnl(account.getUnrealizedPnl() + contract.getUnrealizedPnl());
                }
            }

            BalanceInCurrency balance = new BalanceInfo.BalanceInCurrency(account.getEquity(), // balance
                    account.getRealizedPnl(), // realized PnL,
                    account.getUnrealizedPnl(), // unrealized PnL,
                    Double.NaN, // previousDayBalance,
                    Double.NaN, // netLiquidityValue
                    token, null);// rateToBase)
            tradingListeners.forEach(l -> l.onBalance(new BalanceInfo(Collections.singletonList(balance))));
        }
    }
	
	private void onFuturesAccountRestResponse(FuturesAccount account) {
        if (account.getContracts() != null) {
            List<FutureAccountsContractFixedMargin> contracts = account.getContracts();

            for (FutureAccountsContractFixedMargin contract : contracts) {
                account.setRealizedPnl(account.getRealizedPnl() + contract.getRealizedPnl());
                account.setUnrealizedPnl(account.getUnrealizedPnl() + contract.getUnrealizedPnl());
            }
        }

        BalanceInCurrency balance = new BalanceInfo.BalanceInCurrency(account.getEquity(), // balance
                account.getRealizedPnl(), // realized PnL,
                account.getUnrealizedPnl(), // unrealized PnL,
                Double.NaN, // previousDayBalance,
                Double.NaN, // netLiquidityValue
                account.getCurrency(), null);// rateToBase)
        tradingListeners.forEach(l -> l.onBalance(new BalanceInfo(Collections.singletonList(balance))));
    }
	
    protected void onSpotAccount(List<SpotAccount> accounts) {
        for (SpotAccount account : accounts) {
            String currency = account.getCurrency();

            if (currenciesForSpotBalance.containsKey(currency)) {
                BalanceInCurrency balance = new BalanceInfo.BalanceInCurrency(account.getAvailable(), // balance
                        Double.NaN, // realized PnL,
                        Double.NaN, // unrealized PnL,
                        Double.NaN, // previousDayBalance,
                        Double.NaN, // netLiquidityValue
                        currency, null);// rateToBase)

                tradingListeners.forEach(l -> l.onBalance(new BalanceInfo(Collections.singletonList(balance))));
            }

            if (currenciesForSpotPosition.containsKey(currency)) {

                Set<String> aliases = currenciesForSpotPosition.get(currency);

                for (String alias : aliases) {

                    String baseCurrency = ((InstrumentSpot) genericInstruments.get(alias)).getBaseCurrency();
                    double minSize = ((InstrumentSpot) genericInstruments.get(alias)).getMinSize();

                    aliasedStatusInfos.computeIfAbsent(alias, v -> {
                        StatusInfoLocal infoLocal = new StatusInfoLocal();
                        infoLocal.setInstrumentAlias(alias);
                        infoLocal.setCurrency(Double.valueOf(minSize) + " " + baseCurrency);
                        return infoLocal;
                        });
                    StatusInfoLocal info = aliasedStatusInfos.computeIfPresent(alias, (k, v) -> {
                        v.setPosition((int) Math.round(account.getAvailable() / minSize));
                        v.setCurrency(Double.valueOf(minSize) + " " + baseCurrency);
                        return v;
                        });

                    updateStatus(info);
                }
            }
        }
    }


    @Override
    public void subscribe(SubscribeInfo subscribeInfo) {
        try {
            if (isSubscribed(subscribeInfo, false)) {
                getSubscribed(subscribeInfo);
            }
        } catch (NullPointerException e) {
            if (subscribeInfo == null) {
                Log.info("subscribeInfo is null");
            } else {
                String type = subscribeInfo.type.toLowerCase();
                String symbol = subscribeInfo.symbol;
                String alias = type + "@" + symbol;
                Log.info("Cannot be subscribed to " + alias);
                throw new RuntimeException();
            }
        }
    }

    public void getSubscribed(SubscribeInfo subscribeInfo) {
            String type = subscribeInfo.type.toLowerCase();
            String symbol = subscribeInfo.symbol;
            String alias = type + "@" + symbol;

            singleThreadExecutor.execute(() -> {
                synchronized (aliasInstruments) {
                    getConnector().subscribeOrder(symbol, type);

                    if (type.equals("futures")) {
                        refreshFuturesPosition(alias);

                        getConnector().subscribePositionFutures(symbol, type);
                        String underlyingIndex = ((InstrumentFutures) genericInstruments.get(alias))
                                .getUnderlyingIndex();
                        refreshFuturesAccount(underlyingIndex);

                        getConnector().TEMPsubscribeAccount(symbol, type, underlyingIndex);

                    } else if (type.equals("spot")) {

                        refreshBalance(alias);

                        String baseCurrency = ((InstrumentSpot) genericInstruments.get(alias)).getBaseCurrency();
                        String quoteCurrency = ((InstrumentSpot) genericInstruments.get(alias)).getQuoteCurrency();

                        currenciesForSpotPosition.computeIfAbsent(baseCurrency, v -> new HashSet<String>());
                        currenciesForSpotPosition.computeIfPresent(baseCurrency, (k, v) -> {
                            v.add(alias);
                            return v;
                        });

                        currenciesForSpotBalance.computeIfAbsent(quoteCurrency, v -> new HashSet<String>());
                        currenciesForSpotBalance.computeIfPresent(quoteCurrency, (k, v) -> {
                            v.add(alias);
                            return v;
                        });

                        getConnector().TEMPsubscribeAccount(symbol, type, baseCurrency);
                        getConnector().TEMPsubscribeAccount(symbol, type, quoteCurrency);
                    }

                    refreshOrders(alias);

                }
            });
        
    }
    
    @Override
    public void unsubscribe(String alias) {
        super.unsubscribe(alias);

        int at = alias.indexOf('@');
        String symbol = alias.substring(at + 1);
        String type = alias.substring(0, at);

        getConnector().TEMPunsubscribeOrder(symbol, type);

        if (genericInstruments.get(alias) instanceof InstrumentSpot) {

            String quoteCurrency = ((InstrumentSpot) genericInstruments.get(alias)).getQuoteCurrency();

            currenciesForSpotPosition.computeIfPresent(quoteCurrency, (k, v) -> {
                v.remove(alias);
                return v;
            });

            String baseCurrency = ((InstrumentSpot) genericInstruments.get(alias)).getBaseCurrency();

            currenciesForSpotBalance.computeIfPresent(baseCurrency, (k, v) -> {
                v.remove(alias);
                return v;
            });
        }
    }

    private void refreshBalance(String alias) {
        String type = OkexUtils.getTypeFromALias(alias);

        if (type.equals("spot")) {
            AccountSpot[] accounts;
            try (OkexFuturesRestClient restClient = new OkexFuturesRestClient(apiKey, secretKey)) {
                accounts = restClient.customCall("/api/spot/v3/accounts", "GET", "", AccountSpot[].class, apiKey,
                        secretKey, getConnector().passPhraze, null);
            }

            Map<String, AccountSpot> accountsMap = Arrays.stream(accounts)
                    .collect(Collectors.toMap(AccountSpot::getCurrency, account -> account));

            String quoteCurrency = ((InstrumentSpot) genericInstruments.get(alias)).getQuoteCurrency();
            double minSize = ((InstrumentSpot) genericInstruments.get(alias)).getMinSize();
            AccountSpot quoteCurrencyAccount = accountsMap.get(quoteCurrency);
            double quoteCurrencyBalance = 0.0;

            if (quoteCurrencyAccount != null) {
                quoteCurrencyBalance = quoteCurrencyAccount.getAvailable();
            }

            BalanceInCurrency balance = new BalanceInfo.BalanceInCurrency(
                    quoteCurrencyBalance, // balance
                    Double.NaN, // realized PnL,
                    Double.NaN, // unrealized PnL,
                    Double.NaN, // previousDayBalance,
                    Double.NaN, // netLiquidityValue
                    quoteCurrency, null);// rateToBase)

            tradingListeners.forEach(l -> l.onBalance(new BalanceInfo(Collections.singletonList(balance))));

            String baseCurrency = ((InstrumentSpot) genericInstruments.get(alias)).getBaseCurrency();
            AccountSpot baseCurrencyAccount = accountsMap.get(baseCurrency);

            double baseCurrencyBalance = 0.0;

            if (baseCurrencyAccount != null) {
                baseCurrencyBalance = baseCurrencyAccount.getAvailable();
            }
            
            final double baseCurrencyBalanceFinal = baseCurrencyBalance;
            aliasedStatusInfos.computeIfAbsent(alias, v -> {
                StatusInfoLocal infoLocal = new StatusInfoLocal();
                infoLocal.setInstrumentAlias(alias);
                infoLocal.setCurrency(Double.valueOf(minSize) + " " + baseCurrency);
                return infoLocal;
                });
            StatusInfoLocal info = aliasedStatusInfos.computeIfPresent(alias, (k, v) -> {
                v.setPosition((int) Math.round(baseCurrencyBalanceFinal / minSize));
                v.setCurrency(Double.valueOf(minSize) + " " + baseCurrency);
                return v;
                });

            updateStatus(info);
            
        } else if (type.equals("futures")) {

            String accountsResponse;
            InstrumentGeneric generic = genericInstruments.get(alias);
            InstrumentFutures instrumentFutures = (InstrumentFutures) generic;
            String underlyingInstrument = instrumentFutures.getUnderlyingIndex();
            try (OkexFuturesRestClient restClient = new OkexFuturesRestClient(apiKey, secretKey)) {
                accountsResponse = restClient.customCall("/api/futures/v3/accounts/" + underlyingInstrument, "GET", "",
                        String.class, apiKey, secretKey, getConnector().passPhraze, null);
            }

            if (accountsResponse.contains("\"margin_mode\":\"crossed\"")) {
                try {
                    AccountsFuturesCrossMargin accountsCrossMargin = objectMapper.readValue(accountsResponse,
                            AccountsFuturesCrossMargin.class);
                    BalanceInCurrency balance = new BalanceInfo.BalanceInCurrency(
                            accountsCrossMargin.getTotalAvailBalance(), // balance
                            accountsCrossMargin.getRealizedPnl(), // realized PnL,
                            accountsCrossMargin.getUnrealizedPnl(), // unrealized PnL,
                            Double.NaN, // previousDayBalance,
                            accountsCrossMargin.getEquity(), // netLiquidityValue
                            underlyingInstrument, 1.0);// rateToBase)
                    balanceMap.put(alias, balance);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (accountsResponse.contains("\"margin_mode\":\"fixed\"")) {
                try {
                    AccountsFuturesFixedMarginResponse accountsFixedMarginResponse = objectMapper
                            .readValue(accountsResponse, AccountsFuturesFixedMarginResponse.class);

                    BalanceInCurrency balance = new BalanceInfo.BalanceInCurrency(
                            accountsFixedMarginResponse.getTotalAvailBalance(), // balance
                            accountsFixedMarginResponse.getRealizedPnl(), // realized PnL,
                            accountsFixedMarginResponse.getUnrealizedPnl(), // unrealized PnL,
                            Double.NaN, // previousDayBalance,
                            accountsFixedMarginResponse.getEquity(), // netLiquidityValue
                            underlyingInstrument, 1.0);// rateToBase)
                    balanceMap.put(alias, balance);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        BalanceInfo info = new BalanceInfo(new ArrayList<BalanceInfo.BalanceInCurrency>(balanceMap.values()));
        tradingListeners.forEach(l -> l.onBalance(info));
    }

    private void refreshOrders(String alias) {
        String type = OkexUtils.getTypeFromALias(alias);
        String instrumentId = OkexUtils.getInstrumentIdFromALias(alias);

        if (this instanceof OkexRealTimeTradingProvider) {
            if (type.equals("spot")) {
                List<Map.Entry<String, String>> params = new ArrayList<>();
                params.add(new AbstractMap.SimpleEntry<String, String>("instrument_id", instrumentId));
                params.add(new AbstractMap.SimpleEntry<String, String>("status", "open"));

                OrderDataSpot[] ordersSpot = null;
                try (OkexFuturesRestClient restClient = new OkexFuturesRestClient(apiKey, secretKey)) {
                    String testordersSpot = restClient.customCall("/api/spot/v3/orders", "GET", "", String.class,
                            apiKey, secretKey, getConnector().passPhraze, params);

                    ordersSpot = objectMapper.readValue(testordersSpot, OrderDataSpot[].class);

                    if (ordersSpot == null)
                        return;

                    for (OrderDataSpot spot : ordersSpot) {
                        ((OkexRealTimeTradingProvider) this).onOrder(spot);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (type.equals("futures")) {
                OrdersFuturesList ordersFutures;
                String path = "/api/futures/v3/orders/" + instrumentId;

                List<Map.Entry<String, String>> params = new ArrayList<>();
                params.add(new AbstractMap.SimpleEntry<String, String>("status", "0"));

                try (OkexFuturesRestClient restClient = new OkexFuturesRestClient(apiKey, secretKey)) {
                    ordersFutures = restClient.customCall(path, "GET", "", OrdersFuturesList.class, apiKey, secretKey,
                            getConnector().passPhraze, params);

                    if (ordersFutures == null)
                        return;

                    OrderDataFutures[] futures = ordersFutures.getOrder_info();

                    if (futures == null)
                        return;

                    for (OrderDataFutures future : futures) {
                        ((OkexRealTimeTradingProvider) this).onOrder(future);
                    }
                }
            }
        }
    }

	    private void refreshFuturesPosition(String alias) {
	        String type = OkexUtils.getTypeFromALias(alias);
	        
	        if (!type.equals("futures")) return;
	        
	        String instrumentId = OkexUtils.getInstrumentIdFromALias(alias);
	        
	        if (this instanceof OkexRealTimeTradingProvider) {
	            FuturesPositionsOfCurrencyResponse positionsResponse;
	                String path = "/api/futures/v3/" + instrumentId + "/position";
	                
	                try (OkexFuturesRestClient restClient = new OkexFuturesRestClient(apiKey, secretKey)) {
	                    positionsResponse = restClient.customCall(path, "GET", "",
	                            FuturesPositionsOfCurrencyResponse.class, apiKey, secretKey, getConnector().passPhraze, null);
	                    
	                    if (positionsResponse != null) {
	                        onFuturesPosition(positionsResponse.getHolding());
	                    }
	                }
	        }
	    }
	    
    private void refreshFuturesAccount(String underlyingIndex) {

        if (this instanceof OkexRealTimeTradingProvider) {
            FuturesAccount response;
            String path = "/api/futures/v3/accounts/" + underlyingIndex;

            try (OkexFuturesRestClient restClient = new OkexFuturesRestClient(apiKey, secretKey)) {
                response = restClient.customCall(path, "GET", "", FuturesAccount.class, apiKey, secretKey,
                        getConnector().passPhraze, null);

                if (response != null) {
                    onFuturesAccountRestResponse(response);
                }
            }
        }

    }

    public void onFuturesPosition(SubscribeFuturesPositionResponse response) {
        onFuturesPosition(response.getData());
    }

    public void onFuturesPosition(List<FuturesPosition> data) {
        for (FuturesPosition position : data) {

            int qty = (int) Math.round(-position.getShortAvailQty() + position.getLongAvailQty());

            double avrPrice = Math.abs((-position.getShortAvgCost() * position.getShortAvailQty()
                    + position.getLongAvgCost() * position.getLongAvailQty()) / (double) qty);

            String alias = "futures@" + position.getInstrumentId();

            InstrumentFutures instrument = (InstrumentFutures) genericInstruments.get(alias);
            String currency = "contract worth " + instrument.getContractVal() + " " + instrument.getQuoteCurrency();

            double unrealizedPnl = getUpdatedUnrealizedPnl(alias, qty, avrPrice);

            aliasedStatusInfos.computeIfAbsent(alias, v -> {
                StatusInfoLocal infoLocal = new StatusInfoLocal();
                infoLocal.setInstrumentAlias(alias);
                infoLocal.setCurrency(currency);
                return infoLocal;
            });
            StatusInfoLocal info = aliasedStatusInfos.computeIfPresent(alias, (k, v) -> {
                v.setPosition(qty);
                v.setUnrealizedPnl(unrealizedPnl);
                v.setRealizedPnl(position.getRealizedPnl());
                v.setCurrency(currency);
                return v;
            });

            updateStatus(info);

            positionsMap.put(alias, Pair.of((int) position.getShortAvailQty(), (int) position.getLongAvailQty()));
        }
    }
        
    public double getUpdatedUnrealizedPnl(String alias, int qty, double avrCost) {
        if (qty == 0) {
            return 0.0;
        }
        
        UnrealizedPnlData data = unrealizedPnlMap.computeIfAbsent(alias, v -> new UnrealizedPnlData());
        data.setQty(qty);
        data.setAvrCost(avrCost);
        
        double price = qty > 0 ? data.getLastAskTrade() : data.getLastBidTrade();
        
        if (price == 0.0) {
            return 0.0;
        }
        
        double contractVal = ((InstrumentFutures) genericInstruments.get(alias)).getContractVal();
        double naturalValue = contractVal/avrCost;


        return - naturalValue + Math.abs(qty * contractVal/price);
    }
    
    public double getUpdatedUnrealizedPnl(String alias, boolean isBuy, double lastTrade) {
        UnrealizedPnlData data = unrealizedPnlMap.computeIfAbsent(alias, v -> new UnrealizedPnlData());

        if (isBuy) {
            data.setLastAskTrade(lastTrade);
        } else {
            data.setLastBidTrade(lastTrade);
        }

        int qty = data.getQty();
        if (qty == 0) {
            return 0.0;
        }

        double price = qty > 0 ? data.getLastAskTrade() : data.getLastBidTrade();

        if (price == 0.0) {
            return 0.0;
        }

        double avrCost = data.getAvrCost();

        double contractVal = ((InstrumentFutures) genericInstruments.get(alias)).getContractVal();
        double naturalValue = contractVal/avrCost;

        return - naturalValue + Math.abs(qty * contractVal/price);
        
    }
        
    private void closeFuturesPosition(PlaceOrderRequest workaroundRequest, String workaroundId,
            OrderInfoBuilder orderInfo, String type, int size) {
        workaroundRequest.setType(type);
        ((PlaceOrderRequestFuturesOrSwap) workaroundRequest).setSize(size);
        ((PlaceOrderRequestFuturesOrSwap) workaroundRequest).setMatchPrice("1");

        singleThreadExecutor.submit(() -> {
            Log.debug("+++Enter synchronize");

            bmIdSentOrders.put(workaroundId, orderInfo);

            orderInfo.setStatus(OrderStatus.PENDING_SUBMIT);
            tradingListeners.forEach(l -> l.onOrderUpdated(orderInfo.build()));
            orderInfo.markAllUnchanged();

            PlaceOrderResponse orderResponse = getConnector().placeOrder(workaroundRequest);

            if (!orderResponse.isResult()) {
                Log.info(
                        "REST Order Rejected: " + orderResponse.getErrorCode() + " " + orderResponse.getErrorMessage());
                orderInfo.setStatus(OrderStatus.REJECTED);
                tradingListeners.forEach(l -> l.onOrderUpdated(orderInfo.build()));
                orderInfo.markAllUnchanged();

                adminListeners
                        .forEach(
                                l -> l.onSystemTextMessage(
                                        "Failed to place order with error code: " + orderResponse.getErrorCode() + "\n"
                                                + orderResponse.getErrorMessage(),
                                        SystemTextMessageType.ORDER_FAILURE));
                return;
            }
        });
    }

}
