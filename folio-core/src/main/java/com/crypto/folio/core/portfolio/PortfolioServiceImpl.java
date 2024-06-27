package com.crypto.folio.core.portfolio;

import com.crypto.folio.common.models.Portfolio;

public class PortfolioServiceImpl implements PortfolioService {
    private final PortfolioStore portfolioStore;

    public PortfolioServiceImpl(Portfolio initialValue,
                                PortfolioStore portfolioStore) {
        this.portfolioStore = portfolioStore;
        this.portfolioStore.save(initialValue);
    }

    @Override
    public Portfolio getPortfolio() {
        return portfolioStore.getCurrent();
    }
}
