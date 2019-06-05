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
	public String alias;
	public String table;
	List<Trade> data;
}
