package com.crypto.market.data.impl;

import com.crypto.market.data.MarketDataListener;
import com.crypto.market.data.MarketDataProvider;
import com.crypto.market.data.MarketDataServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MarketDataServiceImplTest {
    private final MarketDataProvider marketDataProvider = mock(MarketDataProvider.class);
    private final MarketDataServiceImpl marketDataServiceImpl = new MarketDataServiceImpl(marketDataProvider);

    private final BigDecimal firstResult = BigDecimal.TEN;
    private ArgumentCaptor<MarketDataListener> captor;

    @BeforeEach
    void init() {
        captor = ArgumentCaptor.forClass(MarketDataListener.class);
        when(marketDataProvider.getPriceAndSubscribe(any(), captor.capture())).thenReturn(firstResult);
    }

    @Test
    void success_priceIsCached_soDontCallTwoTimes() {
        BigDecimal result = marketDataServiceImpl.getPrice("AAPL");
        assert result.compareTo(firstResult) == 0;
        when(marketDataProvider.getPriceAndSubscribe(any(), any())).thenReturn(BigDecimal.valueOf(10393));

        BigDecimal result2 = marketDataServiceImpl.getPrice("AAPL");
        assert result2.compareTo(firstResult) == 0;
    }

    @Test
    void success_priceIsCached_AndUpdatedIfListenerCalled() {
        BigDecimal result = marketDataServiceImpl.getPrice("AAPL");
        assert result.compareTo(firstResult) == 0;
        MarketDataListener listener = captor.getValue();

        listener.onPriceChange("AAPL", BigDecimal.ONE);

        BigDecimal result2 = marketDataServiceImpl.getPrice("AAPL");
        assert result2.compareTo(BigDecimal.ONE) == 0;
    }

    @Test
    void success_priceIsCached_AndNotUpdatedIfListenerCalledForDifferentSymbol() {
        BigDecimal result = marketDataServiceImpl.getPrice("AAPL");
        assert result.compareTo(firstResult) == 0;
        MarketDataListener listener = captor.getValue();

        listener.onPriceChange("GOOGL", BigDecimal.ONE);

        BigDecimal result2 = marketDataServiceImpl.getPrice("AAPL");
        assert result2.compareTo(firstResult) == 0;

        BigDecimal result3 = marketDataServiceImpl.getPrice("GOOGL");
        assert result3.compareTo(BigDecimal.ONE) == 0;
    }

}
