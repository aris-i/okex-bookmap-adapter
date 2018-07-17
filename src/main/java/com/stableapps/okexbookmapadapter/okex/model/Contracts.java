/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stableapps.okexbookmapadapter.okex.model;

import java.util.List;
import lombok.Data;

/**
 *
 * @author aris
 */
@Data
public class Contracts {

	public String symbol;
	public Double balance;
	public List<Contract> contracts = null;
}
