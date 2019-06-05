package com.stableapps.okexbookmapadapter.okex.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class OrderDataSpot extends OrderData {
    
    double notional;
    String side;
    
    @JsonProperty("filled_size")
    double filledSize;

    @JsonProperty("filled_notional")
    double filledNotional;

    @JsonProperty("margin_trading")
    int marginTrading;
    
}
