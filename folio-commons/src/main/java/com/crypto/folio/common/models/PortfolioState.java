package com.crypto.folio.common.models;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class PortfolioState {
    private final int id;
    private final Map<String, StockPriceState> stockPriceStates;
    private final List<PositionState> positionStates;

    private final BigDecimal portfolioNav;

    public PortfolioState(int id, Map<String, StockPriceState> stockPriceStates, List<PositionState> positionStates, BigDecimal portfolioNav) {
        this.id = id;
        this.stockPriceStates = stockPriceStates;
        this.positionStates = positionStates;
        this.portfolioNav = portfolioNav;
    }

    public int getId() {
        return id;
    }

    public Map<String, StockPriceState> getStockPriceStates() {
        return stockPriceStates;
    }

    public List<PositionState> getPositionStates() {
        return positionStates;
    }

    public BigDecimal getPortfolioNav() {
        return portfolioNav;
    }

    @Override
    public String toString() {
        return "PortfolioState{" +
                "id=" + id +
                ", stockPriceStates=" + stockPriceStates +
                ", positionStates=" + positionStates +
                ", portfolioNav=" + portfolioNav +
                '}';
    }
}
