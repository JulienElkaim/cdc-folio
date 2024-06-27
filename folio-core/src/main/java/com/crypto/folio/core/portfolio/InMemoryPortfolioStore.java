package com.crypto.folio.core.portfolio;

import com.crypto.folio.core.portfolio.PortfolioStore;
import com.crypto.folio.common.models.Portfolio;

import java.util.Collections;

public class InMemoryPortfolioStore implements PortfolioStore {
    private static final Portfolio DEFAULT_PORTFOLIO = new Portfolio(Collections.EMPTY_LIST);
    private Portfolio portfolio;
    @Override
    public Portfolio getCurrent() {
        return portfolio == null ? DEFAULT_PORTFOLIO  : portfolio;
    }

    @Override
    public void save(Portfolio portfolio) {
        this.portfolio = portfolio;
    }
}
