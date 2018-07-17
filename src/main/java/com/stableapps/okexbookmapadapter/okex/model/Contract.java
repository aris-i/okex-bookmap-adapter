/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stableapps.okexbookmapadapter.okex.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 *
 * @author aris
 */
@Data
public class Contract {

	public double freeze;
	public double balance;
	@JsonProperty("contract_id")
	public long contractId;
	public double available;
	public double profit;
	public double bond;
}
