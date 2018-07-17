/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stableapps.okexbookmapadapter.okex.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

/**
 *
 * @author aris
 */
@Data
public class PositionsFixedMargin {

	public String symbol;
	@JsonProperty("user_id")
	public long userId;
	public List<PositionFixedMargin> positions = null;

}
