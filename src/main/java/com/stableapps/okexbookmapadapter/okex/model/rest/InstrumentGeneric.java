package com.stableapps.okexbookmapadapter.okex.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class InstrumentGeneric {

    @JsonProperty("instrument_id")
    String instrumentId;
    @JsonProperty("quote_currency")
    String quoteCurrency;
    @JsonProperty("tick_size")
    double tickSize;
}
