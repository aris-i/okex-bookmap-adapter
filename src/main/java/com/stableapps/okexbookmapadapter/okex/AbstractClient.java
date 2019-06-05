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
public abstract class AbstractClient implements	OkexConnector.MarketDepthListener,
		OkexConnector.PositionListener,

		OkexConnector.FuturesPositionListener,
		
		OkexConnector.FuturesAccountListener,
		OkexConnector.SpotAccountListener,
		OkexConnector.TradeRecordListener,
		OkexConnector.OrderListener,
		OkexConnector.ConnectionListener {

}
