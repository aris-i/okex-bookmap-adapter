/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stableapps.okexbookmapadapter.okex.model;

import lombok.Getter;

/**
 *
 * @author aris
 */
public enum OkexOrderStatus {
	Cancelled(-1),
	Unfilled(0),
	PartiallyFilled(1),
	FullyFilled(2),
	CancelRequestInProcess(4);

	@Getter
	private int value;

	OkexOrderStatus(int value) {
		this.value = value;
	}

	public static OkexOrderStatus of(int value){
		switch(value){
			case -1:
				return Cancelled;
			case 0:
				return Unfilled;
			case 1:
				return PartiallyFilled;
			case 2:
				return FullyFilled;
			case 4:
				return CancelRequestInProcess;
			default:
				throw new IllegalArgumentException("This is not a valid value.");
		}
	}
}
