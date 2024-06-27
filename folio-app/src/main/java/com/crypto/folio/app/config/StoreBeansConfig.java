package com.crypto.folio.app.config;

import com.crypto.folio.app.data.InstrumentStoreImpl;
import com.crypto.folio.app.data.OptionDefinitionRepository;
import com.crypto.folio.app.data.StockDefinitionRepository;
import com.crypto.folio.core.portfolio.InMemoryPortfolioStore;
import com.crypto.folio.core.portfolio.PortfolioStore;
import com.crypto.ref.data.InstrumentStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StoreBeansConfig {
    @Bean
    public PortfolioStore portfolioStore() {
        return new InMemoryPortfolioStore();
    }

    @Bean
    public InstrumentStore instrumentStore(OptionDefinitionRepository optionDefinitionRepository,
                                           StockDefinitionRepository stockDefinitionRepository) {
        return new InstrumentStoreImpl(optionDefinitionRepository, stockDefinitionRepository);
    }

}
