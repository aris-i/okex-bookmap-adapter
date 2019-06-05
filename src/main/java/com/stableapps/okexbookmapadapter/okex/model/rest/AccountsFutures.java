package com.stableapps.okexbookmapadapter.okex.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AccountsFutures {

    @JsonProperty("margin_mode")
    String marginMode;
    
    @JsonProperty("total_avail_balance")
    double totalAvailBalance;

    double equity;
}
