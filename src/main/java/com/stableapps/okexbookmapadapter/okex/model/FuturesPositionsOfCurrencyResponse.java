package com.stableapps.okexbookmapadapter.okex.model;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class FuturesPositionsOfCurrencyResponse {

    boolean result;
    
    @JsonProperty("margin_mode")
    String marginMode;
    
    ArrayList<FuturesPosition> holding;
}
