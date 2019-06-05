package com.stableapps.okexbookmapadapter.okex.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AccountsFuturesFixedMarginContract {

    @JsonProperty("available_qty")
    double availableQty;

    @JsonProperty("fixed_balance")
    double fixedBalance;

    @JsonProperty("instrument_id")
    String instrumentId;
    
    @JsonProperty("margin_for_unfilled")
    double marginForUnfilled;
    
    @JsonProperty("margin_frozen")
    double marginFrozen;
    
    @JsonProperty("realized_pnl")
    double realizedPnl;
    
    @JsonProperty("unrealized_pnl")
    double unrealizedPnl;
}
