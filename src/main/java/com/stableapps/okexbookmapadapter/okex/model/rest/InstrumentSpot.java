package com.stableapps.okexbookmapadapter.okex.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class InstrumentSpot extends InstrumentGeneric {

    @JsonProperty("base_currency")
    String baseCurrency;
    @JsonProperty("min_size")
    double minSize;
    @JsonProperty("size_increment")
    double sizeIncrement;
}
