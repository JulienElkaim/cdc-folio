package com.crypto.folio.price.engine;

import com.crypto.ref.data.models.InstrumentDefinition;

public abstract class PriceEngine extends PriceResolver<InstrumentDefinition> {
    /* Strong typing only for now */
    /* Could hold common logic for Price Engines as a Cache, Throttler etc based on ctx.*/
}
