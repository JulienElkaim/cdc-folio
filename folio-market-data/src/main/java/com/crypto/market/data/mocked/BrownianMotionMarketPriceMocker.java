package com.crypto.market.data.mocked;

import com.crypto.folio.common.utils.SystemClock;
import com.crypto.ref.data.InstrumentService;
import com.crypto.ref.data.models.InstrumentDefinition;
import com.crypto.ref.data.models.StockDefinition;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

/**
 * Time Geometric Brownian Motion Generator
 */
public class BrownianMotionMarketPriceMocker implements MarketPriceGenerator {
    private static final int ANNUALIZING_FACTOR = 7257600; // Assumes 8 hours trading, during 252 days.
    private final InstrumentService instrumentService;

    private final Map<String, BigDecimal> initialSpotPrices;
    private final BigDecimal unsupportedStocksInitialPrice;
    private final SystemClock systemClock;
    private final Instant timeReference;
    private final Random randomGenerator;

    public BrownianMotionMarketPriceMocker(InstrumentService instrumentService,
                                           SystemClock systemClock,
                                           Map<String, BigDecimal> initialSpotPrices,
                                           BigDecimal unsupportedStocksInitialPrice,
                                           Instant timeReference,
                                           Random randomGenerator) {
        this.instrumentService = instrumentService;
        this.initialSpotPrices = Objects.requireNonNull(initialSpotPrices);
        this.unsupportedStocksInitialPrice = unsupportedStocksInitialPrice;
        this.timeReference = timeReference;
        this.randomGenerator = randomGenerator;
        this.systemClock = systemClock;
    }

    @Override
    public BigDecimal generate(String symbol) {
        double timeDelta = ChronoUnit.SECONDS.between(timeReference, systemClock.nowInstant());
        double epsilon = randomGenerator.nextGaussian();
        InstrumentDefinition definition = instrumentService.getDefinition(symbol);

        if (definition instanceof StockDefinition) {
            StockDefinition stockDefinition = (StockDefinition) definition;
            double expectedReturn = stockDefinition.getExpectedReturn();
            double annualizedStd = stockDefinition.getAnnualizedStd();

            double driftTerm = expectedReturn * (timeDelta / ANNUALIZING_FACTOR);
            double diffusionTerm = Math.sqrt(timeDelta / ANNUALIZING_FACTOR) * annualizedStd * epsilon;
            BigDecimal initialSpot = initialSpotPrices.getOrDefault(symbol, unsupportedStocksInitialPrice);

            return BigDecimal.valueOf(Math.max(0, driftTerm + diffusionTerm + initialSpot.doubleValue()));
        }

        throw new IllegalArgumentException("Only stock is supported for MarketPriceMocker.");
    }
}
