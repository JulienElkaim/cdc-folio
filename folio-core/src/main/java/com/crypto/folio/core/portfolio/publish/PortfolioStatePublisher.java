package com.crypto.folio.core.portfolio.publish;

import com.crypto.folio.common.models.PortfolioState;

public interface PortfolioStatePublisher {
    void publish(PortfolioState portfolio);

    void publishError(String errorMessage);
}
