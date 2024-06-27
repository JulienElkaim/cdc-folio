package com.crypto.ref.data.models;


import com.crypto.folio.common.models.InstrumentType;

public interface InstrumentDefinition {
    String getSymbol();
    InstrumentType getInstrumentType();
}
