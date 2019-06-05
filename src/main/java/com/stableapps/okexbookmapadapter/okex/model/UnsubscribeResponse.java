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
public class UnsubscribeResponse extends Message {

	int binary;
	String channel;
	String event;
	UnsubscribedData data;
}
