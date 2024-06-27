package com.crypto.folio.price.engine.impl;

import com.crypto.market.data.MarketDataService;
import com.crypto.ref.data.models.StockDefinition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StockPriceResolverTest {
    private final MarketDataService marketDataService = mock(MarketDataService.class);
    private final StockPriceResolver stockPriceResolver = new StockPriceResolver(marketDataService);
    private final String symbol = "AAPL";
    private final StockDefinition stockDefinition = mock(StockDefinition.class);
    @BeforeEach
    void init(){
        when(stockDefinition.getSymbol()).thenReturn(symbol);
        when(marketDataService.getPrice(symbol)).thenReturn(BigDecimal.TEN);
    }

    @Test
    void resolve_success(){
        assertEquals(BigDecimal.TEN, stockPriceResolver.resolve(stockDefinition));
    }

    @Test
    void resolve_fail_whenMarketPriceNull(){
        when(marketDataService.getPrice(symbol)).thenReturn(null);
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> stockPriceResolver.resolve(stockDefinition));
        assertEquals("Price not found for stock: AAPL", thrown.getMessage());
    }

    @Test
    void resolve_success_defaultToZeroIfMarketNegative(){
        when(marketDataService.getPrice(symbol)).thenReturn(BigDecimal.valueOf(-100));
        assertEquals(BigDecimal.ZERO, stockPriceResolver.resolve(stockDefinition));
    }

    @Test
    void getSupportedClass() {
        assertEquals(StockDefinition.class, stockPriceResolver.getSupportedClass());
    }
}
