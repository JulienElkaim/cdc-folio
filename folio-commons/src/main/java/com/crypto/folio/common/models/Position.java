package com.crypto.folio.common.models;

public class Position {
    private final String ticker;
    private final int quantity;

    public Position(String ticker, int quantity) {
        this.ticker = ticker;
        this.quantity = quantity;
    }

    public String getTicker() {
        return ticker;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public String toString() {
        return "PortfolioItem{" +
                "ticker='" + ticker + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}
