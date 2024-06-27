package com.crypto.folio.core.portfolio;

import com.crypto.folio.common.models.Portfolio;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class PortfolioServiceImplTest {
    private final PortfolioStore portfolioStore = mock(PortfolioStore.class);
    private final Portfolio initialValue = mock(Portfolio.class);

    @BeforeEach
    void init() {
        when(portfolioStore.getCurrent()).thenReturn(initialValue);
    }

    @Test
    void testGetPortfolio() {
        PortfolioServiceImpl portfolioServiceImpl = new PortfolioServiceImpl(initialValue, portfolioStore);
        Portfolio result = portfolioServiceImpl.getPortfolio();
        Assertions.assertSame(initialValue, result);
    }

    @Test
    void constructor_SaveInitialValueInStore() {
        verify(portfolioStore, never()).save(initialValue);
        new PortfolioServiceImpl(initialValue, portfolioStore);
        verify(portfolioStore, times(1)).save(initialValue);
    }
}
