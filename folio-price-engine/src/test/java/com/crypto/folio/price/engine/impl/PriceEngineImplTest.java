package com.crypto.folio.price.engine.impl;

import com.crypto.folio.common.models.OptionType;
import com.crypto.folio.price.engine.PriceResolver;
import com.crypto.ref.data.models.InstrumentDefinition;
import com.crypto.ref.data.models.OptionDefinition;
import com.crypto.ref.data.models.StockDefinition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PriceEngineImplTest {
    private final OptionPriceResolver optionPriceResolver = mock(OptionPriceResolver.class);
    private final StockPriceResolver stockPriceResolver = mock(StockPriceResolver.class);
    private final Map marketValueComputers = new HashMap<Class<? extends InstrumentDefinition>,
            PriceResolver<? extends InstrumentDefinition>>() {{
        put(OptionDefinition.class, optionPriceResolver);
        put(StockDefinition.class, stockPriceResolver);
    }};

    private final PriceEngineImpl priceEngine = new PriceEngineImpl(marketValueComputers);

    private final StockDefinition stockDefinition = new StockDefinition("A", 0.1, 0.2);
    private final OptionDefinition optionDefinition = new OptionDefinition("B", "C", LocalDate.MIN, BigDecimal.ZERO, OptionType.PUT);

    @BeforeEach
    void init() {
        when(stockPriceResolver.resolve(any())).thenReturn(BigDecimal.TEN);
        when(optionPriceResolver.resolve(any())).thenReturn(BigDecimal.ONE);
    }

    @Test
    void success() {

        BigDecimal price = priceEngine.resolve(stockDefinition);
        Assertions.assertTrue(price.compareTo(BigDecimal.TEN) == 0);

        price = priceEngine.resolve(optionDefinition);
        Assertions.assertTrue(price.compareTo(BigDecimal.ONE) == 0);
    }

    @Test
    void success_defaultPriceToZero() {
        when(stockPriceResolver.resolve(any())).thenReturn(BigDecimal.valueOf(-100));

        BigDecimal price = priceEngine.resolve(stockDefinition);
        Assertions.assertTrue(price.compareTo(BigDecimal.ZERO) == 0);
    }

    @Test
    void fail_throwExceptionIfNoResolver() {
        InstrumentDefinition instrumentDefinition = mock(InstrumentDefinition.class);
        RuntimeException thrown = Assertions.assertThrows(RuntimeException.class, () -> priceEngine.resolve(instrumentDefinition));
        assertEquals("Market value computer not found for instrument type: " + instrumentDefinition.getClass(), thrown.getMessage());
    }

    @Test
    void getSupportedClass() {
        assertEquals(InstrumentDefinition.class, priceEngine.getSupportedClass());
    }


}
