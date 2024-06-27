package com.crypto.folio.app.watcher;

import com.crypto.folio.common.models.PortfolioState;
import com.crypto.folio.core.portfolio.publish.PortfolioStatePublisher;
import com.crypto.folio.core.portfolio.state.PortfolioStateProvider;
import org.springframework.scheduling.annotation.Scheduled;

public class PortfolioStateWatcher {
    private final PortfolioStateProvider portfolioStateProvider;
    private final PortfolioStatePublisher publisher;

    public PortfolioStateWatcher(PortfolioStateProvider portfolioStateProvider,
                                 PortfolioStatePublisher publisher) {
        this.portfolioStateProvider = portfolioStateProvider;
        this.publisher = publisher;
    }

    @Scheduled(fixedRateString = "${portfolio.refresh.rate.ms}")
    public void watchState() {
        try {
            PortfolioState portfolioState = portfolioStateProvider.compute();
            publisher.publish(portfolioState);
        } catch (Exception e) {
            publisher.publishError(e.getMessage());
        }
    }
}
