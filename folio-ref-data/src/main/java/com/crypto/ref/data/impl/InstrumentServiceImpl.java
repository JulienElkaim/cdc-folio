package com.crypto.ref.data.impl;

import com.crypto.folio.common.models.OptionType;
import com.crypto.ref.data.InstrumentStore;
import com.crypto.ref.data.models.InstrumentDefinition;
import com.crypto.ref.data.InstrumentService;
import com.crypto.ref.data.models.OptionDefinition;
import com.crypto.ref.data.models.StockDefinition;

import java.math.BigDecimal;
import java.time.LocalDate;

public class InstrumentServiceImpl implements InstrumentService {
    private final InstrumentStore instrumentStore;

    public InstrumentServiceImpl(InstrumentStore instrumentStore) {
        this.instrumentStore = instrumentStore;
    }

    @Override
    public InstrumentDefinition getDefinition(String symbol) {
        return instrumentStore.getDefinition(symbol);
    }
}
