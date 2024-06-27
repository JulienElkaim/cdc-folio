package com.crypto.folio.price.engine.impl;

import com.crypto.folio.common.models.OptionType;
import com.crypto.folio.common.utils.SystemClock;
import com.crypto.folio.price.engine.PriceResolver;
import com.crypto.ref.data.InstrumentService;
import com.crypto.ref.data.models.InstrumentDefinition;
import com.crypto.ref.data.models.OptionDefinition;
import com.crypto.ref.data.models.StockDefinition;
import org.apache.commons.math3.distribution.RealDistribution;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;

public class OptionPriceResolver extends PriceResolver<OptionDefinition> {
    private final PriceResolver<StockDefinition> stockPriceProvider;
    private final InstrumentService instrumentService;
    private final SystemClock systemClock;
    private final RealDistribution probabilisticDistribution;
    private final double rfr;

    public OptionPriceResolver(PriceResolver<StockDefinition> stockPriceProvider,
                               InstrumentService instrumentService,
                               SystemClock systemClock,
                               RealDistribution probabilisticDistribution, // Parent interface cause 99% of time normalDistrib, some instr. can be different.
                               double yearConstantRiskFree) {
        this.stockPriceProvider = stockPriceProvider;
        this.instrumentService = instrumentService;
        this.rfr = yearConstantRiskFree;
        this.probabilisticDistribution = probabilisticDistribution;
        this.systemClock = systemClock;
    }

    @Override
    public Class<OptionDefinition> getSupportedClass() {
        return OptionDefinition.class;
    }

    @Override
    protected BigDecimal internalResolve(OptionDefinition instrumentDefinition) {
        if (instrumentDefinition == null) {
            throw new IllegalArgumentException("OptionDefinition received is null, can't compute price.");
        }

        InstrumentDefinition underlyingDefinition = instrumentService.getDefinition(instrumentDefinition.getUnderlyingSymbol());
        if (!(underlyingDefinition instanceof StockDefinition)) {
            throw new IllegalArgumentException("Only stock is supported as underlying instrument for OptionPriceResolver.");
        }

        StockDefinition stockDefinition = (StockDefinition) underlyingDefinition;
        double spot = stockPriceProvider.resolve(stockDefinition).doubleValue();

        long time = ChronoUnit.YEARS.between(systemClock.nowLocalDate(), instrumentDefinition.getExpiryDate());
        double std = stockDefinition.getAnnualizedStd();
        double strike = instrumentDefinition.getStrikePrice().doubleValue();

        double d1 = computeD1(spot, strike, std, time);
        double d2 = computeD2(d1, std, time);

        BigDecimal price = OptionType.CALL == instrumentDefinition.getOptionType() ?
                computeCallPrice(spot, d1, d2, strike, time) :
                computePutPrice(spot, d1, d2, strike, time);
        return price.max(BigDecimal.ZERO);
    }

    private BigDecimal computePutPrice(double spot, double d1, double d2, double strike, long time) {
        return BigDecimal.valueOf(strike * Math.exp(-rfr * time) * probabilisticDistribution.cumulativeProbability(-d2) - (spot * probabilisticDistribution.cumulativeProbability(-d1)));
    }

    private BigDecimal computeCallPrice(double spot, double d1, double d2, double strike, long time) {
        return BigDecimal.valueOf(spot * probabilisticDistribution.cumulativeProbability(d1) - (strike * Math.exp(-rfr * time) * probabilisticDistribution.cumulativeProbability(d2)));
    }

    private double computeD2(double d1, double std, long time) {
        return d1 - std * Math.sqrt(time);
    }

    private double computeD1(double spot, double strike, double std, long time) {
        return (Math.log(spot / strike) + (rfr + Math.pow(std, 2) / 2) * time) / (std * Math.sqrt(time));
    }
}
