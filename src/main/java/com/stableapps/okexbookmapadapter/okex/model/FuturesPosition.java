package com.stableapps.okexbookmapadapter.okex.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FuturesPosition {

    /* for fixed margin */
    @JsonProperty("long_qty")
    double longQty;
    @JsonProperty("long_avail_qty")
    double longAvailQty;
    @JsonProperty("long_margin")
    double longMargin;
    @JsonProperty("long_liqui_price")
    double longLiquiPrice;
    @JsonProperty("long_pnl_ratio")
    double longPnlRatio;
    @JsonProperty("long_avg_cost")
    double longAvgCost;
    @JsonProperty("long_settlement_price")
    double longSettlmentPrice;
    @JsonProperty("realised_pnl")
    double realizedPnl;
    @JsonProperty("short_qty")
    double shortQty;
    @JsonProperty("short_avail_qty")
    double shortAvailQty;
    @JsonProperty("short_margin")
    double shortMargin;
    @JsonProperty("short_liqui_price")
    double shortLiquiPrice;
    @JsonProperty("short_pnl_ratio")
    double shortPnlRatio;
    @JsonProperty("short_avg_cost")
    double shortAvgCost;
    @JsonProperty("short_settlement_price")
    double shortSettlmentPrice;
    @JsonProperty("instrument_id")
    String instrumentId;
    @JsonProperty("long_leverage")
    int longLeverage;
    @JsonProperty("short_leverage")
    int shortLeverage;
    @JsonProperty("created_at")
    String createdAt;
    @JsonProperty("updated_at")
    String updatedAt;
    @JsonProperty("margin_mode")
    String marginMode;
    
    /* for cross margin */
    @JsonProperty("force_liqu_price")
    double forceLiquPrice;
    int leverage;
    @JsonProperty("liquidation_price")
    double liquidationPrice;

}
