package com.crypto.folio.price.engine.impl;

import com.crypto.folio.common.models.OptionType;
import com.crypto.folio.common.utils.SystemClock;
import com.crypto.folio.price.engine.PriceResolver;
import com.crypto.ref.data.InstrumentService;
import com.crypto.ref.data.models.OptionDefinition;
import com.crypto.ref.data.models.StockDefinition;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OptionPriceResolverTest {
    private final PriceResolver<StockDefinition> stockPriceProvider = (PriceResolver<StockDefinition>) mock(PriceResolver.class);
    private final InstrumentService instrumentService = mock(InstrumentService.class);
    private final SystemClock systemClock = mock(SystemClock.class);
    private final NormalDistribution realDistribution = mock(NormalDistribution.class);

    private final double riskFreeRate = 0.05;
    private final OptionPriceResolver optionPriceResolver = new OptionPriceResolver(stockPriceProvider, instrumentService, systemClock, realDistribution, riskFreeRate);

    private final OptionDefinition optionDefinition = mock(OptionDefinition.class);
    private final String underlyingSymbol = "CDC";


    private final String symbol = "CDC_CALL_1100_2024_01_01";
    private final LocalDate expiryDate = LocalDate.of(2029, 1, 1);
    private final LocalDate nowDate = LocalDate.of(2024, 1, 1);
    private final BigDecimal strikePrice = BigDecimal.valueOf(1100);
    private final BigDecimal spotPrice = BigDecimal.valueOf(1000);
    private final double annualizedStd = 0.2;

    private final double expectedD1 = 0.5695037511347284;
    private final double expectedD2 = 0.12229015563477041;

    private final double cumProbD1 = 0.7154928371013081;
    private final double cumProbD2 = 0.5486653859261152;

    private final StockDefinition stockDefinition = mock(StockDefinition.class);

    @BeforeEach
    void init() {
        when(optionDefinition.getSymbol()).thenReturn(symbol);
        when(optionDefinition.getUnderlyingSymbol()).thenReturn(underlyingSymbol);
        when(optionDefinition.getExpiryDate()).thenReturn(expiryDate);
        when(optionDefinition.getStrikePrice()).thenReturn(strikePrice);
        when(optionDefinition.getOptionType()).thenReturn(OptionType.CALL);

        when(instrumentService.getDefinition(underlyingSymbol)).thenReturn(stockDefinition);
        when(systemClock.nowLocalDate()).thenReturn(nowDate);

        when(stockPriceProvider.resolve(stockDefinition)).thenReturn(spotPrice);
        when(stockDefinition.getAnnualizedStd()).thenReturn(annualizedStd);
        when(realDistribution.cumulativeProbability(eq(expectedD2))).thenReturn(cumProbD2);
        when(realDistribution.cumulativeProbability(eq(expectedD1))).thenReturn(cumProbD1);
        when(realDistribution.cumulativeProbability(eq(-expectedD2))).thenReturn(1 - cumProbD2);
        when(realDistribution.cumulativeProbability(eq(-expectedD1))).thenReturn(1 - cumProbD1);
    }

    @Test
    void resolveSuccess_callOption() {
        BigDecimal price = optionPriceResolver.resolve(optionDefinition);
        BigDecimal expectedPriceCall = BigDecimal.valueOf(245.4617016775316);
        assertBigDecimalAreEqual(expectedPriceCall, price, 5);
    }

    @Test
    void resolveSuccess_putOption() {
        when(optionDefinition.getOptionType()).thenReturn(OptionType.PUT);
        BigDecimal price = optionPriceResolver.resolve(optionDefinition);
        BigDecimal expectedPriceCall = BigDecimal.valueOf(102.142563056077);
        assertBigDecimalAreEqual(expectedPriceCall, price, 5);
    }

    @Test
    void resolve_cantBeLessThan0_callOption() {
        // Unrealistic negative standard deviation
        when(stockDefinition.getAnnualizedStd()).thenReturn(-annualizedStd);
        BigDecimal price = optionPriceResolver.resolve(optionDefinition);
        Assertions.assertEquals(price, BigDecimal.ZERO);
    }

    @Test
    void resolve_cantBeLessThan0_putOption() {
        when(optionDefinition.getOptionType()).thenReturn(OptionType.PUT);
        // Unrealistic Cumulative Prob at 2 (200%?!)
        when(realDistribution.cumulativeProbability(anyDouble())).thenReturn(2.0);

        BigDecimal price = optionPriceResolver.resolve(optionDefinition);
        Assertions.assertEquals(price, BigDecimal.ZERO);
    }

    @Test
    void resolve_throws_ifNullDefinitionPassed() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> optionPriceResolver.resolve(null));
        assertEquals("OptionDefinition received is null, can't compute price.", thrown.getMessage());
    }

    @Test
    void resolve_throws_ifNullUnderlyingInstrumentDefinition() {
        when(instrumentService.getDefinition(underlyingSymbol)).thenReturn(null);
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> optionPriceResolver.resolve(optionDefinition));
        assertEquals("Only stock is supported as underlying instrument for OptionPriceResolver.", thrown.getMessage());
    }

    private void assertBigDecimalAreEqual(BigDecimal bd1, BigDecimal bd2, int precision) {
        BigDecimal bd1Precision = bd1.setScale(precision, RoundingMode.HALF_UP);
        BigDecimal bd2Precision = bd2.setScale(precision, RoundingMode.HALF_UP);
        Assertions.assertTrue(bd1Precision.compareTo(bd2Precision) == 0);
    }

    @Test
    void getSupportedClass() {
        assertEquals(OptionDefinition.class, optionPriceResolver.getSupportedClass());
    }
}
