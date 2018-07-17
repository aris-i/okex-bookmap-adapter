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
public enum MatchPrice {
	No(0),
	Yes(1);

	@Getter
	private int value;

	MatchPrice(int value) {
		this.value = value;
	}
}
