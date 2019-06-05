package com.stableapps.okexbookmapadapter.okex.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FuturesAccount {
    double equity;
    double margin;
    @JsonProperty("margin_mode")
    String marginMode;
    @JsonProperty("margin_ratio")
    double marginRatio;
    @JsonProperty("realized_pnl")
    double realizedPnl;
    @JsonProperty("total_avail_balance")
    double totalAvailableBalance;
    @JsonProperty("unrealized_pnl")
    double unrealizedPnl;
    
    List<FutureAccountsContractFixedMargin> contracts;
    
    String currency;
    
    
    @JsonProperty("fixed_balance")
    double fixedBalance;
    @JsonProperty("available_qty")
    double availQuantity;
    @JsonProperty("margin_frozen")
    double marginFrozen;
    @JsonProperty("margin_for_unfilled")
    double marginForUnfilled;
    @JsonProperty("auto_margin")
    int autoMargin;
    @JsonProperty("liqui_mode")
    String liqui_mode;
    
}
