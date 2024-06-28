package com.crypto.market.data.mocked;

import com.crypto.folio.common.utils.SystemClock;
import com.crypto.ref.data.InstrumentService;
import com.crypto.ref.data.models.InstrumentDefinition;
import com.crypto.ref.data.models.StockDefinition;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Time Geometric Brownian Motion Generator
 */
public class BrownianMotionMarketPriceMocker implements MarketPriceGenerator {
    private static final int ANNUALIZING_FACTOR = 7257600; // Assumes 8 hours trading, during 252 days.
    private final InstrumentService instrumentService;
    private final Map<String, StockPriceSnapshot> priceReferences;
    private final BigDecimal unsupportedStocksInitialPrice;
    private final SystemClock systemClock;
    private final Instant unsupportedStockTimeReference;
    private final Random randomGenerator;

    public BrownianMotionMarketPriceMocker(InstrumentService instrumentService,
                                           SystemClock systemClock,
                                           Map<String, StockPriceSnapshot> priceReferences,
                                           BigDecimal unsupportedStocksInitialPrice,
                                           Instant unsupportedStockTimeReference,
                                           Random randomGenerator) {
        this.instrumentService = instrumentService;
        this.unsupportedStockTimeReference = unsupportedStockTimeReference;
        this.priceReferences = priceReferences == null ? new HashMap<>() : new ConcurrentHashMap<>(priceReferences);
        this.unsupportedStocksInitialPrice = unsupportedStocksInitialPrice;
        this.randomGenerator = randomGenerator;
        this.systemClock = systemClock;
    }

    @Override
    public BigDecimal generate(String symbol) {
        StockPriceSnapshot currentSnapshot = priceReferences.compute(symbol, (id, prevSnapshot) -> {
                    if (prevSnapshot == null) {
                        prevSnapshot = new StockPriceSnapshot(
                                symbol, unsupportedStocksInitialPrice, unsupportedStockTimeReference);
                    }
                    StockPriceSnapshot newSnapshot = generate(prevSnapshot);
                    return newSnapshot.getPrice().compareTo(prevSnapshot.getPrice()) != 0 ? newSnapshot : prevSnapshot;
                }
        );

        return currentSnapshot.getPrice();
    }

    public StockPriceSnapshot generate(StockPriceSnapshot priceSnapshot) {
        Instant nowInstant = systemClock.nowInstant();
        double timeDelta = ChronoUnit.SECONDS.between(priceSnapshot.getTimeReference(), nowInstant);
        double epsilon = randomGenerator.nextGaussian();
        InstrumentDefinition definition = instrumentService.getDefinition(priceSnapshot.getSymbol());

        if (definition instanceof StockDefinition) {
            StockDefinition stockDefinition = (StockDefinition) definition;
            double expectedReturn = stockDefinition.getExpectedReturn();
            double annualizedStd = stockDefinition.getAnnualizedStd();

            double driftTerm = expectedReturn * (timeDelta / ANNUALIZING_FACTOR);
            double diffusionTerm = Math.sqrt(timeDelta / ANNUALIZING_FACTOR) * annualizedStd * epsilon;

            BigDecimal newPrice = BigDecimal.valueOf(Math.max(0, driftTerm + diffusionTerm + priceSnapshot.getPrice().doubleValue()));
            return new StockPriceSnapshot(priceSnapshot.getSymbol(), newPrice, nowInstant);
        }

        throw new IllegalArgumentException("Only stock is supported for MarketPriceMocker.");
    }
}
