/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stableapps.okexbookmapadapter.okex.model;

import lombok.Data;

/**
 *
 * @author aris
 */
@Data
public class SubscribeFuturesAccountInitialResponse extends Message {

	public int binary;
	public String event;
	public String channel;
	public InitialResponse data = null;
}
