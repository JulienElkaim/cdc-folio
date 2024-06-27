package com.crypto.market.data.mocked;

import com.crypto.folio.common.utils.SystemClock;
import com.crypto.ref.data.InstrumentService;
import com.crypto.ref.data.models.OptionDefinition;
import com.crypto.ref.data.models.StockDefinition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BrownianMotionMarketPriceMockerTest {
    private final InstrumentService instrumentService = mock(InstrumentService.class);
    private final BigDecimal unsupportedStocksInitPrice = BigDecimal.TEN;
    private final Map<String, BigDecimal> initialSpotPrices = new HashMap<String, BigDecimal>() {{
        put("AAPL", BigDecimal.ONE);
    }};
    private final Instant timeReference = Instant.EPOCH;
    private final SystemClock systemClock = mock(SystemClock.class);
    private final Random randomGen = mock(Random.class);
    private final StockDefinition stockDefinition = mock(StockDefinition.class);
    private final String symbol = "AAPL";
    private final double expectedReturn = 0.1;
    private final double annualizedStd = 0.2;
    private final BrownianMotionMarketPriceMocker mocker = new BrownianMotionMarketPriceMocker(instrumentService, systemClock, initialSpotPrices, unsupportedStocksInitPrice, timeReference, randomGen);

    @BeforeEach
    void init() {
        when(systemClock.nowInstant()).thenReturn(Instant.EPOCH.plus(100, ChronoUnit.DAYS));
        when(randomGen.nextGaussian()).thenReturn(0.5);

        when(stockDefinition.getSymbol()).thenReturn(symbol);
        when(stockDefinition.getExpectedReturn()).thenReturn(expectedReturn);
        when(stockDefinition.getAnnualizedStd()).thenReturn(annualizedStd);
        when(instrumentService.getDefinition(symbol)).thenReturn(stockDefinition);
    }

    @Test
    void generate_success() {
        BigDecimal generate = mocker.generate(symbol);
        Assertions.assertTrue(BigDecimal.valueOf(1.2281565641656154).compareTo(generate) == 0);
    }

    @Test
    void generate_success_initialPriceDefaulted() {
        when(instrumentService.getDefinition("MAUK")).thenReturn(stockDefinition);
        BigDecimal generate = mocker.generate("MAUK");
        Assertions.assertTrue(BigDecimal.valueOf(10.228156564165616).compareTo(generate) == 0);
    }

    @Test
    void generate_instrumentIsNotStock() {
        when(instrumentService.getDefinition(symbol)).thenReturn(mock(OptionDefinition.class));
        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> mocker.generate(symbol));
        Assertions.assertEquals("Only stock is supported for MarketPriceMocker.", thrown.getMessage());
    }

}
