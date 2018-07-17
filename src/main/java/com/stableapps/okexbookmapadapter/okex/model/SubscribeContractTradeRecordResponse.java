/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stableapps.okexbookmapadapter.okex.model;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 *
 * @author aris
 */
@Data
public class SubscribeContractTradeRecordResponse extends Message {

	public int binary;
	public String channel;
	private List<List<String>> data = null;

	List<Trade> tradeRecords;

	public void setData(List<List<String>> data) {
		this.data = data;
		tradeRecords = new ArrayList<>();
		Trade tradeRecord = null;
		for (List<String> d : data) {
			tradeRecord = Trade.builder()
					.tid(Long.valueOf(d.get(0)))
					.price(Double.valueOf(d.get(1)))
					.amount(Double.valueOf(d.get(2)))
					.time(d.get(3))
					.type(d.get(4))
					.build();
			tradeRecords.add(tradeRecord);
		}
	}

}
