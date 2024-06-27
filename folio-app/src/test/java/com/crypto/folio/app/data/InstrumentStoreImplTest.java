package com.crypto.folio.app.data;

import com.crypto.ref.data.models.InstrumentDefinition;
import com.crypto.ref.data.models.OptionDefinition;
import com.crypto.ref.data.models.StockDefinition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class InstrumentStoreImplTest {
    private OptionDefinitionRepository optionDefinitionRepository = mock(OptionDefinitionRepository.class);
    private StockDefinitionRepository stockDefinitionRepository = mock(StockDefinitionRepository.class);
    private InstrumentStoreImpl instrumentStore = new InstrumentStoreImpl(optionDefinitionRepository, stockDefinitionRepository);

    private final OptionDefinition optionDefinition = mock(OptionDefinition.class);
    private final StockDefinition stockDefinition = mock(StockDefinition.class);
    private final String optionSymbol = "option";
    private final String stockSymbol = "stock";
    private final String unknownSymbol = "unknown";

    @BeforeEach
    void init() {
        when(optionDefinitionRepository.findById(optionSymbol)).thenReturn(Optional.of(optionDefinition));
        when(optionDefinitionRepository.findById(stockSymbol)).thenReturn(Optional.empty());
        when(optionDefinitionRepository.findById(unknownSymbol)).thenReturn(Optional.empty());

        when(stockDefinitionRepository.findById(stockSymbol)).thenReturn(Optional.of(stockDefinition));
        when(stockDefinitionRepository.findById(optionSymbol)).thenReturn(Optional.empty());
        when(stockDefinitionRepository.findById(unknownSymbol)).thenReturn(Optional.empty());

    }

    @Test
    void getDefinition_foundInOptionRepository() {
        InstrumentDefinition definition = instrumentStore.getDefinition(optionSymbol);
        assertSame(optionDefinition, definition);
    }


    @Test
    void getDefinition_foundInStockRepository() {
        InstrumentDefinition definition = instrumentStore.getDefinition(stockSymbol);
        assertSame(stockDefinition, definition);
    }

    @Test
    void getDefinition_notFound() {
        assertNull(instrumentStore.getDefinition(unknownSymbol));
    }
}
