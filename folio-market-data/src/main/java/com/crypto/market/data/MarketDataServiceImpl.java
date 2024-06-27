package com.crypto.market.data;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MarketDataServiceImpl implements MarketDataService {
    private final Map<String, BigDecimal> priceCache = new ConcurrentHashMap<>();
    private final MarketDataProvider marketDataProvider;

    public MarketDataServiceImpl(MarketDataProvider marketDataProvider) {
        this.marketDataProvider = marketDataProvider;
    }

    @Override
    public BigDecimal getPrice(String symbol) {
        return priceCache.computeIfAbsent(symbol, key ->
                marketDataProvider.getPriceAndSubscribe(symbol, priceCache::put));
    }
}
