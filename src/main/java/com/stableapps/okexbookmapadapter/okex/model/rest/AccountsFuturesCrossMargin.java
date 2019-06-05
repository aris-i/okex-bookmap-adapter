package com.stableapps.okexbookmapadapter.okex.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AccountsFuturesCrossMargin extends AccountsFutures{

    double margin;

    @JsonProperty("realized_pnl")
    double realizedPnl;
    
    @JsonProperty("unrealized_pnl")
    double unrealizedPnl;
    
    @JsonProperty("margin_ratio")
    double marginRatio;
}
