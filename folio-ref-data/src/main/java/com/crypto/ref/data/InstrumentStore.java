package com.crypto.ref.data;

import com.crypto.ref.data.models.InstrumentDefinition;

public interface InstrumentStore {

    InstrumentDefinition getDefinition(String symbol);
}
