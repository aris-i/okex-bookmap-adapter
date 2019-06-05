package com.stableapps.okexbookmapadapter.okex.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize(using = PlaceOrderRequestTokenOrMarginSerializer.class)
public class PlaceOrderRequestTokenOrMargin extends PlaceOrderRequest {

	String side;
    @JsonProperty("instrument_id")
	String instrumentId;
    @JsonProperty("order_type")
	String duration;
    @JsonProperty("margin_trading")
	int marginTrading;

    double price;
    int notional;
}
