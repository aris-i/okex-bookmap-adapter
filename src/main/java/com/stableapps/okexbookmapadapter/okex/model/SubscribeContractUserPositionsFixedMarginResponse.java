/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stableapps.okexbookmapadapter.okex.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author aris
 */
public class SubscribeContractUserPositionsFixedMarginResponse extends Message {

	public boolean binary;
	public String channel;
	public PositionsFixedMargin data;
}
