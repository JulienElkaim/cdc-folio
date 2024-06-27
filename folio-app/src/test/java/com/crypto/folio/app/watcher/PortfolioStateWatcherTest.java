package com.crypto.folio.app.watcher;

import com.crypto.folio.common.models.PortfolioState;
import com.crypto.folio.core.portfolio.publish.PortfolioStatePublisher;
import com.crypto.folio.core.portfolio.state.PortfolioStateProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

public class PortfolioStateWatcherTest {
    private final PortfolioStateProvider provider = mock(PortfolioStateProvider.class);
    private final PortfolioStatePublisher publisher = mock(PortfolioStatePublisher.class);
    private final PortfolioStateWatcher watcher = new PortfolioStateWatcher(provider, publisher);

    private final PortfolioState portfolioState = mock(PortfolioState.class);

    @BeforeEach
    void init() {
        when(provider.compute()).thenReturn(portfolioState);
    }

    @Test
    void watchState() {
        watcher.watchState();
        verify(provider).compute();
        ArgumentCaptor<PortfolioState> captor = ArgumentCaptor.forClass(PortfolioState.class);
        verify(publisher).publish(captor.capture());
        PortfolioState stateCaptured = captor.getValue();
        Assertions.assertSame(portfolioState, stateCaptured);
    }

    @Test
    void watchState_providerThrows_publishError() {
        when(provider.compute()).thenThrow(new RuntimeException("myError"));
        watcher.watchState();
        verify(provider).compute();
        verify(publisher, never()).publish(any());
        verify(publisher).publishError("myError");
    }

    @Test
    void watchState_publisherPublishThrows_publishError() {
        Mockito.doThrow(new RuntimeException("My Bad")).when(publisher).publish(any());
        watcher.watchState();
        verify(provider).compute();
        verify(publisher).publish(any());
        verify(publisher).publishError("My Bad");
    }
}
