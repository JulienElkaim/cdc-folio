package com.crypto.folio.common.models;

import java.math.BigDecimal;

public class PositionState {
    private final String symbol;
    private final int quantity;

    private final BigDecimal price;
    private final BigDecimal marketValue;

    public PositionState(String symbol,
                         int quantity,
                         BigDecimal price,
                         BigDecimal marketValue) {
        this.symbol = symbol;
        this.quantity = quantity;
        this.price = price;
        this.marketValue = marketValue;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getMarketValue() {
        return marketValue;
    }

    public BigDecimal getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "PositionState{" +
                "symbol='" + symbol + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", marketValue=" + marketValue +
                '}';
    }
}
