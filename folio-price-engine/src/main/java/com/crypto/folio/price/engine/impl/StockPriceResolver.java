package com.crypto.folio.price.engine.impl;

import com.crypto.folio.price.engine.PriceResolver;
import com.crypto.market.data.MarketDataService;
import com.crypto.ref.data.models.StockDefinition;

import java.math.BigDecimal;

public class StockPriceResolver extends PriceResolver<StockDefinition> {
    private final MarketDataService marketDataService;

    public StockPriceResolver(MarketDataService marketDataService) {
        this.marketDataService = marketDataService;
    }

    @Override
    public Class<StockDefinition> getSupportedClass() {
        return StockDefinition.class;
    }

    @Override
    protected BigDecimal internalResolve(StockDefinition instrumentDefinition) {
        return this.getPrice(instrumentDefinition.getSymbol());
    }

    private BigDecimal getPrice(String symbol) {
        BigDecimal price = marketDataService.getPrice(symbol);
        if (price == null) {
            throw new RuntimeException("Price not found for stock: " + symbol);
        }
        return price.max(BigDecimal.ZERO);
    }
}
