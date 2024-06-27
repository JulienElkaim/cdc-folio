package com.crypto.folio.core.portfolio;

import com.crypto.folio.common.models.Portfolio;

/**
 * N.B: This layer allow for future enhancement on storing data.
 * Storage layer is separated from the Service layer.
 */
public interface PortfolioStore {
    Portfolio getCurrent();

    void save(Portfolio portfolio);
}
