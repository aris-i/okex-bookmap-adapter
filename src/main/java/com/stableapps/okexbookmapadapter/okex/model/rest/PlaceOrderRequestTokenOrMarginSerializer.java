package com.stableapps.okexbookmapadapter.okex.model.rest;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class PlaceOrderRequestTokenOrMarginSerializer extends StdSerializer<PlaceOrderRequestTokenOrMargin>  {

    public PlaceOrderRequestTokenOrMarginSerializer() {
        super(PlaceOrderRequestTokenOrMargin.class);
    }
    
    protected PlaceOrderRequestTokenOrMarginSerializer(Class<PlaceOrderRequestTokenOrMargin> t) {
        super(t);
    }

    @Override
    public void serialize(PlaceOrderRequestTokenOrMargin order, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonGenerationException {
        jgen.writeStartObject();
        jgen.writeStringField("client_oid", order.getClientOrderId());
        jgen.writeStringField("type", order.getType().toString()); // attention
        jgen.writeStringField("side", order.getSide()); // attention
        jgen.writeStringField("instrument_id", order.getInstrumentId());
        jgen.writeNumberField("margin_trading", order.getMarginTrading());
        jgen.writeStringField("order_type", order.getDuration());

        if (order.getType().equals("limit")) {
            jgen.writeNumberField("price", order.getPrice());
            jgen.writeNumberField("size", order.getFloatingPointSize());
        } else if (order.getType().equals("market")) {
            if (order.getSide().equals("buy")) {
                jgen.writeNumberField("size", order.getFloatingPointSize());
            } else {
                jgen.writeNumberField("notional", order.getFloatingPointSize());
            }
        }
        jgen.writeEndObject();
    }
}
