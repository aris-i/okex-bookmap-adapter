package com.stableapps.okexbookmapadapter.okex.model.rest;


import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class InstrumentFutures extends InstrumentGeneric {

    @JsonProperty("underlying_index")
    String underlyingIndex;
    @JsonProperty("contract_val")
    int contractVal;
    String listing;
    String delivery;
    @JsonProperty("trade_increment")
    int tradeIncrement;
    @JsonProperty("alias")
    String delivery_week;
}
