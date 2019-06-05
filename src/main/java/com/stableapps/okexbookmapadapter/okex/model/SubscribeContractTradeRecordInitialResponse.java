/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stableapps.okexbookmapadapter.okex.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.Data;

/**
 *
 * @author aris
 */
@Data
@JsonDeserialize(using = CustomSubscribeContractTradeRecordInitialResponseDeserializer.class)
public class SubscribeContractTradeRecordInitialResponse extends Message {

	public int binary;
	public String channel;
	public InitialResponse data;
}
