package com.stableapps.okexbookmapadapter.okex.model;

import lombok.Data;

@Data
public class ErrorWs extends Message{

    String event;
    String message;
    String errorCode;
}
