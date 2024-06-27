package com.crypto.folio.core.portfolio;

import com.crypto.folio.common.models.Portfolio;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;

public class InMemoryPortfolioStoreTest {

    @Test
    void save_replaceCurrentPortfolio() {
        InMemoryPortfolioStore inMemoryPortfolioStore = new InMemoryPortfolioStore();
        Portfolio portfolio = mock(Portfolio.class);
        inMemoryPortfolioStore.save(portfolio);

        Portfolio current = inMemoryPortfolioStore.getCurrent();
        Assertions.assertSame(portfolio, current);
    }

    @Test
    void getCurrent_DefaultPortfolio_when_noneSaved() {
        InMemoryPortfolioStore inMemoryPortfolioStore = new InMemoryPortfolioStore();
        Portfolio current = inMemoryPortfolioStore.getCurrent();

        assert current.getPositions().isEmpty();
    }

    @Test
    void getCurrent_DefaultPortfolio_when_SavedNull() {
        InMemoryPortfolioStore inMemoryPortfolioStore = new InMemoryPortfolioStore();
        inMemoryPortfolioStore.save(null);
        Portfolio current = inMemoryPortfolioStore.getCurrent();
        assert current.getPositions().isEmpty();
    }
}
