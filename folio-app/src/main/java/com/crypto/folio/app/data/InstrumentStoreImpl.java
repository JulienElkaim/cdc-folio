package com.crypto.folio.app.data;

import com.crypto.ref.data.InstrumentStore;
import com.crypto.ref.data.models.InstrumentDefinition;
import com.crypto.ref.data.models.OptionDefinition;
import com.crypto.ref.data.models.StockDefinition;

import java.util.Optional;

public class InstrumentStoreImpl implements InstrumentStore {
    private final OptionDefinitionRepository optionDefinitionRepository;
    private final StockDefinitionRepository stockDefinitionRepository;

    public InstrumentStoreImpl(OptionDefinitionRepository optionDefinitionRepository,
                               StockDefinitionRepository stockDefinitionRepository) {
        this.optionDefinitionRepository = optionDefinitionRepository;
        this.stockDefinitionRepository = stockDefinitionRepository;
    }

    @Override
    public InstrumentDefinition getDefinition(String symbol) {
        Optional<OptionDefinition> optionDefinition = optionDefinitionRepository.findById(symbol);
        if (optionDefinition.isPresent()) {
            return optionDefinition.get();
        }

        Optional<StockDefinition> stockDefinition = stockDefinitionRepository.findById(symbol);
        return stockDefinition.orElse(null);
    }
}
