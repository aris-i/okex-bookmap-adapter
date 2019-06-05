package com.stableapps.okexbookmapadapter.okex.utils;

import java.util.HashMap;
import java.util.Map;

public enum Instrument {
    SPOT, MARGIN, FUTURES, SWAP, ETT;

    public static Map<Instrument, String> map = new HashMap<>();
    
    static {
        map.put(Instrument.SPOT, "spot");
        map.put(Instrument.MARGIN, "margin");
        map.put(Instrument.FUTURES, "futures");
        map.put(Instrument.SWAP, "swap");
        map.put(Instrument.ETT, "ett");
    }

}
