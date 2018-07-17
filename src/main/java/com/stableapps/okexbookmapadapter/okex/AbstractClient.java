/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stableapps.okexbookmapadapter.okex;

/**
 *
 * @author aris
 */
public abstract class AbstractClient implements OkexConnector.MarketPriceListener,
		OkexConnector.CandlestickChartDataListener, OkexConnector.MarketDepthListener,
		OkexConnector.TradeRecordListener, OkexConnector.IndexPriceListener,
		OkexConnector.ForecastPriceListener, OkexConnector.OrderListener,
		OkexConnector.PositionsFixedMarginListener, OkexConnector.ContractsFixedMarginListener,
		OkexConnector.ConnectionListener {
}
