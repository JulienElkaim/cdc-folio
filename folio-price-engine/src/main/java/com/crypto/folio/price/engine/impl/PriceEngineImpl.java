package com.crypto.folio.price.engine.impl;

import com.crypto.folio.price.engine.PriceEngine;
import com.crypto.folio.price.engine.PriceResolver;
import com.crypto.ref.data.models.InstrumentDefinition;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class PriceEngineImpl extends PriceEngine {
    private final Map<Class<? extends InstrumentDefinition>,
            PriceResolver<? extends InstrumentDefinition>> marketValueComputers;

    public PriceEngineImpl(Map<Class<? extends InstrumentDefinition>,
            PriceResolver<? extends InstrumentDefinition>> marketValueComputers) {
        this.marketValueComputers = new HashMap<>(marketValueComputers);
    }

    @Override
    public Class<InstrumentDefinition> getSupportedClass() {
        return InstrumentDefinition.class;
    }

    @Override
    protected BigDecimal internalResolve(InstrumentDefinition instrumentDefinition) {
        PriceResolver<?> priceResolver = marketValueComputers.get(instrumentDefinition.getClass());
        if (priceResolver == null) {
            throw new RuntimeException("Market value computer not found for instrument type: " + instrumentDefinition.getClass());
        }

        return priceResolver.resolve(instrumentDefinition).max(BigDecimal.ZERO);
    }
}
