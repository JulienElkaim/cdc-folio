package com.crypto.folio.common.models;

import java.math.BigDecimal;

public class StockPriceState {
    private final String symbol;
    private final BigDecimal currentPrice;
    private final BigDecimal previousPrice;

    public StockPriceState(String symbol, BigDecimal currentPrice, BigDecimal previousPrice) {
        this.symbol = symbol;
        this.currentPrice = currentPrice;
        this.previousPrice = previousPrice;
    }

    public String getSymbol() {
        return symbol;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public BigDecimal getPreviousPrice() {
        return previousPrice;
    }
}
