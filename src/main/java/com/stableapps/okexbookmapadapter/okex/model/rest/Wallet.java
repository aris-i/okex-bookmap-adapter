package com.stableapps.okexbookmapadapter.okex.model.rest;

import lombok.Data;

@Data
public class Wallet {

    double balance;
    double available;
    String currency;
    double hold;
    
}
