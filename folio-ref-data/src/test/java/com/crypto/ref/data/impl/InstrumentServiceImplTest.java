package com.crypto.ref.data.impl;

import com.crypto.ref.data.InstrumentStore;
import com.crypto.ref.data.models.InstrumentDefinition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class InstrumentServiceImplTest {
    private final InstrumentStore instrumentStore = mock(InstrumentStore.class);
    private final InstrumentServiceImpl instrumentService = new InstrumentServiceImpl(instrumentStore);

    @Test
    public void testGetDefinition_delegatesToStore() {
        String symbol = "AAPL";
        InstrumentDefinition expected = mock(InstrumentDefinition.class);
        when(instrumentStore.getDefinition(symbol)).thenReturn(expected);

        InstrumentDefinition result = instrumentService.getDefinition(symbol);
        Assertions.assertSame(expected, result);
    }
}
