package com.crypto.market.data.mocked;

import com.crypto.market.data.MarketDataListener;
import com.crypto.market.data.MarketDataProvider;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

public class MockedMarketDataProvider implements MarketDataProvider {
    private final MockMarketPulsar mockMarketPulsar;
    private final MarketPriceGenerator marketPriceGenerator;
    private final Map<String, ScheduledFuture<?>> mockedTickersMap;

    public MockedMarketDataProvider(MockMarketPulsar mockMarketPulsar,
                                    MarketPriceGenerator marketPriceGenerator,
                                    Map<String, ScheduledFuture<?>> initialMockTickerMap) {
        this.mockMarketPulsar = mockMarketPulsar;
        this.marketPriceGenerator = marketPriceGenerator;
        this.mockedTickersMap = new ConcurrentHashMap<>(initialMockTickerMap);
    }


    @Override
    public BigDecimal getPriceAndSubscribe(String symbol, MarketDataListener listener) {
        BigDecimal initialPrice = marketPriceGenerator.generate(symbol);

        mockedTickersMap.computeIfAbsent(symbol, key ->
                mockMarketPulsar.runAtPulse(() ->
                        listener.onPriceChange(key, marketPriceGenerator.generate(symbol))
                ));
        return initialPrice;
    }
}
