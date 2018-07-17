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
public class SubscribeContractCandlestickChartDataResponse extends Message {

	public int binary;
	public String channel;
	public List<List<String>> data = null;

	List<CandlestickChartData> candlestickChartDatas;

	public void setData(List<List<String>> data) {
		this.data = data;
		candlestickChartDatas = new ArrayList<>();
		CandlestickChartData candlestickChartData;
		for (List<String> d : data) {
			candlestickChartData = CandlestickChartData.builder()
			  .time(Long.valueOf(d.get(0)))
			  .openPrice(Double.valueOf(d.get(1)))
			  .highestPrice(Double.valueOf(d.get(2)))
			  .lowestPrice(Double.valueOf(d.get(3)))
			  .closePrice(Double.valueOf(d.get(4)))
			  .volume(Double.valueOf(d.get(5)))
			  .unknown(Double.valueOf(d.get(6)))
			  .build();
			candlestickChartDatas.add(candlestickChartData);
		}

	}

}
