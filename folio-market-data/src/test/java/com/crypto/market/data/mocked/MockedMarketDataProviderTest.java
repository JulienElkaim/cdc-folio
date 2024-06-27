package com.crypto.market.data.mocked;

import com.crypto.market.data.MarketDataListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class MockedMarketDataProviderTest {
    private final MockMarketPulsar mockMarketPulsar = mock(MockMarketPulsar.class);
    private final MarketPriceGenerator marketPriceGenerator = mock(MarketPriceGenerator.class);
    private final Map<String, ScheduledFuture<?>> initialMockTickerMap = new HashMap<>();

    private final ScheduledFuture scheduledFuture = mock(ScheduledFuture.class);

    private final MockedMarketDataProvider mockedMarketDataProvider = new MockedMarketDataProvider(mockMarketPulsar, marketPriceGenerator, initialMockTickerMap);

    private final String symbol = "A";
    private final String symbol2 = "B";

    private ArgumentCaptor<Runnable> captor;


    @BeforeEach
    void init() {
        captor = ArgumentCaptor.forClass(Runnable.class);
        when(mockMarketPulsar.runAtPulse(captor.capture())).thenReturn(scheduledFuture);

        when(marketPriceGenerator.generate(symbol)).thenReturn(BigDecimal.ONE);
        when(marketPriceGenerator.generate(symbol2)).thenReturn(BigDecimal.TEN);

    }

    @Test
    void success() {
        MarketDataListener listener = mock(MarketDataListener.class);
        BigDecimal result = mockedMarketDataProvider.getPriceAndSubscribe(symbol, listener);
        assert result.compareTo(BigDecimal.ONE) == 0;

        BigDecimal result2 = mockedMarketDataProvider.getPriceAndSubscribe(symbol2, listener);
        assert result2.compareTo(BigDecimal.TEN) == 0;
    }

    @Test
    void success_wePassedTheListenerToThePulsarAndItIsCalledWithCorrectValues() {
        MarketDataListener listener = mock(MarketDataListener.class);
        BigDecimal result = mockedMarketDataProvider.getPriceAndSubscribe(symbol, listener);
        assert result.compareTo(BigDecimal.ONE) == 0;

        Runnable runnablePassedToPulsar = captor.getValue();

        verify(listener, never()).onPriceChange(any(), any());
        runnablePassedToPulsar.run();
        verify(listener).onPriceChange(symbol, BigDecimal.ONE);
    }

    @Test
    void success_weDontPassToPulsarIfSymbolAlreadySeen() {
        MarketDataListener listener = mock(MarketDataListener.class);
        BigDecimal result = mockedMarketDataProvider.getPriceAndSubscribe(symbol, listener);
        assert result.compareTo(BigDecimal.ONE) == 0;

        Runnable runnablePassedToPulsar = captor.getValue();

        verify(listener, never()).onPriceChange(any(), any());
        runnablePassedToPulsar.run();
        verify(listener).onPriceChange(symbol, BigDecimal.ONE);


        verify(mockMarketPulsar).runAtPulse(any());
        mockedMarketDataProvider.getPriceAndSubscribe(symbol, listener);
        verifyNoMoreInteractions(mockMarketPulsar);
    }
}
