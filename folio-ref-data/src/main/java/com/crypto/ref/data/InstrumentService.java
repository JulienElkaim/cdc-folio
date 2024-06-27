package com.crypto.ref.data;

import com.crypto.ref.data.models.InstrumentDefinition;

public interface InstrumentService {

    InstrumentDefinition getDefinition(String symbol);
}
