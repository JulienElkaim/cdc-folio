package com.crypto.folio.price.engine;

import com.crypto.ref.data.models.InstrumentDefinition;

import java.math.BigDecimal;

/**
 * N.B: This class is meant to be extended once for each InstrumentDefinition.
 * This pattern allow for well-casted record to be passed to each resolver in a Manager pattern.
 * See PriceEngine(Impl) for Manager pattern.
 *
 * @param <T>
 */
public abstract class PriceResolver<T extends InstrumentDefinition> {

    public abstract Class<T> getSupportedClass();

    protected abstract BigDecimal internalResolve(T instrumentDefinition);

    public BigDecimal resolve(InstrumentDefinition instrumentDefinition) {
        return internalResolve(getSupportedClass().cast(instrumentDefinition));
    }
}
