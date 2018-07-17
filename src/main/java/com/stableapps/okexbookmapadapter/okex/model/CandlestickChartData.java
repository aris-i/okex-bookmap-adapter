/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stableapps.okexbookmapadapter.okex.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author aris
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandlestickChartData {

	public long time;
	public double openPrice;
	public double highestPrice;
	public double lowestPrice;
	public double closePrice;
	public double volume;
	public double unknown;
}
