package com.crypto.folio.app;

import com.crypto.folio.app.data.OptionDefinitionRepository;
import com.crypto.folio.app.data.StockDefinitionRepository;
import com.crypto.folio.common.models.OptionType;
import com.crypto.ref.data.models.OptionDefinition;
import com.crypto.ref.data.models.StockDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * N.B: Only useful at first run or when adding new items in Portfolio.
 * Fill database with relevant Instrument Definitions.
 * Can be commented out after first run.
 */
@Component
public class DatabaseFeeder implements ApplicationRunner {
    private final OptionDefinitionRepository optionDefinitionRepository;
    private final StockDefinitionRepository stockDefinitionRepository;

    @Autowired
    public DatabaseFeeder(OptionDefinitionRepository optionDefinitionRepository,
                          StockDefinitionRepository stockDefinitionRepository) {
        this.optionDefinitionRepository = optionDefinitionRepository;
        this.stockDefinitionRepository = stockDefinitionRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        stockDefinitionRepository.saveAll(STOCKS_DEFINITIONS);
        optionDefinitionRepository.saveAll(OPTIONS_DEFINITIONS);
    }

    private static final List<StockDefinition> STOCKS_DEFINITIONS = new ArrayList<StockDefinition>() {{
        add(new StockDefinition("AAPL", 0.4, 0.5));
        add(new StockDefinition("TESLA", 0.25, 0.6));
    }};

    private static final List<OptionDefinition> OPTIONS_DEFINITIONS = new ArrayList<OptionDefinition>() {{
        add(new OptionDefinition(
                "AAPL-OCT2024-120-C",
                "AAPL",
                LocalDate.of(2024, 10, 1),
                BigDecimal.valueOf(120),
                OptionType.CALL));

        add(new OptionDefinition(
                "AAPL-OCT2025-140-P",
                "AAPL",
                LocalDate.of(2025, 10, 1),
                BigDecimal.valueOf(140),
                OptionType.PUT));

        add(new OptionDefinition(
                "TESLA-SEPT2027-300-C",
                "TESLA",
                LocalDate.of(2027, 9, 1),
                BigDecimal.valueOf(300),
                OptionType.CALL));

        add(new OptionDefinition(
                "TESLA-NOV2029-340-P",
                "TESLA",
                LocalDate.of(2029, 11, 1),
                BigDecimal.valueOf(340),
                OptionType.PUT));
    }};
}
