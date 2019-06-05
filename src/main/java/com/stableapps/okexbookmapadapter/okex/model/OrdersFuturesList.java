package com.stableapps.okexbookmapadapter.okex.model;

import lombok.Data;

@Data
public class OrdersFuturesList {

    String result;
    OrderDataFutures[] order_info;
}
