package com.stableapps.okexbookmapadapter.okex.model;

import lombok.Data;

/**
 *
 * @author aris
 */
@Data
public class SubscribeContractsUserInfoFixedMarginResponse extends Message {

	public boolean binary;
	public String channel;
	public Contracts data;
}
