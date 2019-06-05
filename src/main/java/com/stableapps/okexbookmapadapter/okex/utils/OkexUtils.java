package com.stableapps.okexbookmapadapter.okex.utils;

import velox.api.layer1.data.OrderDuration;

public class OkexUtils {
    
    public static String getDurationType(OrderDuration duration) {
        switch (duration) {
        case GTC:
            return "0";
        case GTC_PO:
            return "1";
        case FOK:
            return "2";
        case IOC:
            return "3";
        default:
            return "0";
        }
    }
    
    public static String getTypeFromALias(String alias) {
        int at = alias.indexOf("@");
        String type = alias.substring(0, at);
        return type;
    }
    
    public static String getInstrumentIdFromALias(String alias) {
        int at = alias.indexOf("@");
        String instrumentId = alias.substring(at + 1);
        return instrumentId;
    }
    
    public static boolean isSpot(String alias) {
        return getTypeFromALias(alias).equals("spot");
    }
    
    public static boolean isFutures(String alias) {
        return getTypeFromALias(alias).equals("futures");
    }

}
