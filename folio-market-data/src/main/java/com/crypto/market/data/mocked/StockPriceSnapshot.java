package com.crypto.market.data.mocked;

import java.math.BigDecimal;
import java.time.Instant;

public class StockPriceSnapshot {
    private final String symbol;
    private final BigDecimal price;
    private final Instant timeReference;

    public StockPriceSnapshot(String symbol, BigDecimal price, Instant timeReference) {
        this.symbol = symbol;
        this.price = price;
        this.timeReference = timeReference;
    }

    public String getSymbol() {
        return symbol;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Instant getTimeReference() {
        return timeReference;
    }
}
