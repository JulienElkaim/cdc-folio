package com.crypto.folio.core.portfolio.state;


import com.crypto.folio.common.models.PortfolioState;

public interface PortfolioStateProvider {
    PortfolioState compute();
}
