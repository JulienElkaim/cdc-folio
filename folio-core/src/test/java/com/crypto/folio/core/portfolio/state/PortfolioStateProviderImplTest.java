package com.crypto.folio.core.portfolio.state;

import com.crypto.folio.common.models.InstrumentType;
import com.crypto.folio.common.models.Portfolio;
import com.crypto.folio.common.models.PortfolioState;
import com.crypto.folio.common.models.Position;
import com.crypto.folio.core.portfolio.PortfolioService;
import com.crypto.folio.price.engine.PriceResolver;
import com.crypto.ref.data.InstrumentService;
import com.crypto.ref.data.models.InstrumentDefinition;
import com.crypto.ref.data.models.OptionDefinition;
import com.crypto.ref.data.models.StockDefinition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PortfolioStateProviderImplTest {
    private final PortfolioService portfolioService = mock(PortfolioService.class);
    private final InstrumentService instrumentService = mock(InstrumentService.class);
    private final PriceResolver<InstrumentDefinition> pricer = mock(PriceResolver.class);

    private final int decimalPrecision = 2;
    private final Portfolio portfolio = new Portfolio(new ArrayList<Position>() {{
        add(new Position("AAPL", 10));
        add(new Position("GOOGL", 20));
    }});
    private PortfolioStateProviderImpl portfolioStateService;
    private final StockDefinition appleDefinition = mock(StockDefinition.class);
    private final StockDefinition googleDefinition = mock(StockDefinition.class);
    private final BigDecimal applePrice = new BigDecimal(100);
    private final BigDecimal googlePrice = new BigDecimal(50);
    private final BigDecimal teslaCallPrice = new BigDecimal(10);
    private final BigDecimal teslaPrice = new BigDecimal(1000);


    private OptionDefinition optionDefinition = mock(OptionDefinition.class);
    private StockDefinition teslaDefinition = mock(StockDefinition.class);

    @BeforeEach
    void init() {
        portfolioStateService = new PortfolioStateProviderImpl(portfolioService, instrumentService, pricer, decimalPrecision);

        when(portfolioService.getPortfolio()).thenReturn(portfolio);
        when(instrumentService.getDefinition("AAPL")).thenReturn(appleDefinition);
        when(appleDefinition.getInstrumentType()).thenReturn(InstrumentType.STOCK);
        when(pricer.resolve(appleDefinition)).thenReturn(applePrice);

        when(instrumentService.getDefinition("GOOGL")).thenReturn(googleDefinition);
        when(googleDefinition.getInstrumentType()).thenReturn(InstrumentType.STOCK);
        when(pricer.resolve(googleDefinition)).thenReturn(googlePrice);

        when(instrumentService.getDefinition("TESLA_CALL")).thenReturn(optionDefinition);
        when(optionDefinition.getUnderlyingSymbol()).thenReturn("TESLA");
        when(optionDefinition.getInstrumentType()).thenReturn(InstrumentType.OPTION);
        when(pricer.resolve(optionDefinition)).thenReturn(teslaCallPrice);

        when(instrumentService.getDefinition("TESLA")).thenReturn(teslaDefinition);
        when(teslaDefinition.getInstrumentType()).thenReturn(InstrumentType.STOCK);
        when(pricer.resolve(teslaDefinition)).thenReturn(teslaPrice);
        when(teslaDefinition.getSymbol()).thenReturn("TESLA");
    }

    @Test
    void compute_correctState() {
        PortfolioState portfolioState = portfolioStateService.compute();

        Assertions.assertEquals(0, BigDecimal.valueOf(2000).compareTo(portfolioState.getPortfolioNav()));
        Assertions.assertEquals(2, portfolioState.getPositionStates().size());
        Assertions.assertEquals(0, BigDecimal.valueOf(1000).compareTo(portfolioState.getPositionStates().get(0).getMarketValue()));
        Assertions.assertEquals(0, BigDecimal.valueOf(1000).compareTo(portfolioState.getPositionStates().get(1).getMarketValue()));
        Assertions.assertEquals(1, portfolioState.getId());
        Assertions.assertEquals(2, portfolioState.getStockPriceStates().size());
        Assertions.assertEquals(0, BigDecimal.valueOf(100).compareTo(portfolioState.getStockPriceStates().get("AAPL").getCurrentPrice()));
        Assertions.assertNull(portfolioState.getStockPriceStates().get("AAPL").getPreviousPrice());
        Assertions.assertEquals(0, BigDecimal.valueOf(50).compareTo(portfolioState.getStockPriceStates().get("GOOGL").getCurrentPrice()));
        Assertions.assertNull(portfolioState.getStockPriceStates().get("GOOGL").getPreviousPrice());
    }

    @Test
    void compute_correctState_SecondRun_nothingChanged() {
        portfolioStateService.compute();
        PortfolioState portfolioState = portfolioStateService.compute();
        Assertions.assertEquals(0, BigDecimal.valueOf(2000).compareTo(portfolioState.getPortfolioNav()));
        Assertions.assertEquals(2, portfolioState.getPositionStates().size());
        Assertions.assertEquals(0, BigDecimal.valueOf(1000).compareTo(portfolioState.getPositionStates().get(0).getMarketValue()));
        Assertions.assertEquals(0, BigDecimal.valueOf(1000).compareTo(portfolioState.getPositionStates().get(1).getMarketValue()));
        Assertions.assertEquals(2, portfolioState.getId());
        Assertions.assertEquals(2, portfolioState.getStockPriceStates().size());
        Assertions.assertEquals(0, BigDecimal.valueOf(100).compareTo(portfolioState.getStockPriceStates().get("AAPL").getCurrentPrice()));
        Assertions.assertEquals(0, BigDecimal.valueOf(100).compareTo(portfolioState.getStockPriceStates().get("AAPL").getPreviousPrice()));
        Assertions.assertEquals(0, BigDecimal.valueOf(50).compareTo(portfolioState.getStockPriceStates().get("GOOGL").getCurrentPrice()));
        Assertions.assertEquals(0, BigDecimal.valueOf(50).compareTo(portfolioState.getStockPriceStates().get("GOOGL").getPreviousPrice()));
    }

    @Test
    void compute_correctState_SecondRun_googlePriceChanged() {
        portfolioStateService.compute();
        when(pricer.resolve(googleDefinition)).thenReturn(BigDecimal.valueOf(51.00000));

        PortfolioState portfolioState = portfolioStateService.compute();
        Assertions.assertEquals(0, BigDecimal.valueOf(2020).compareTo(portfolioState.getPortfolioNav()));
        Assertions.assertEquals(2, portfolioState.getPositionStates().size());
        Assertions.assertEquals(0, BigDecimal.valueOf(1000).compareTo(portfolioState.getPositionStates().get(0).getMarketValue()));
        Assertions.assertEquals(0, BigDecimal.valueOf(1020).compareTo(portfolioState.getPositionStates().get(1).getMarketValue()));
        Assertions.assertEquals(2, portfolioState.getId());
        Assertions.assertEquals(2, portfolioState.getStockPriceStates().size());

        Assertions.assertEquals(0, BigDecimal.valueOf(100).compareTo(portfolioState.getStockPriceStates().get("AAPL").getCurrentPrice()));
        Assertions.assertEquals(0, BigDecimal.valueOf(100).compareTo(portfolioState.getStockPriceStates().get("AAPL").getPreviousPrice()));
        Assertions.assertEquals(0, BigDecimal.valueOf(51).compareTo(portfolioState.getStockPriceStates().get("GOOGL").getCurrentPrice()));
        Assertions.assertEquals(0, BigDecimal.valueOf(50).compareTo(portfolioState.getStockPriceStates().get("GOOGL").getPreviousPrice()));
    }

    @Test
    void compute_withOptions_underlyingStockHasStockState() {
        when(portfolioService.getPortfolio()).thenReturn(new Portfolio(
                new ArrayList<Position>() {{
                    add(new Position("AAPL", 10));
                    add(new Position("GOOGL", 20));
                    add(new Position("TESLA_CALL", 5));
                }}
        ));

        PortfolioState portfolioState = portfolioStateService.compute();
        Assertions.assertEquals(0, BigDecimal.valueOf(2050).compareTo(portfolioState.getPortfolioNav()));
        Assertions.assertEquals(3, portfolioState.getPositionStates().size());
        Assertions.assertEquals(0, BigDecimal.valueOf(1000).compareTo(portfolioState.getPositionStates().get(0).getMarketValue()));
        Assertions.assertEquals(0, BigDecimal.valueOf(1000).compareTo(portfolioState.getPositionStates().get(1).getMarketValue()));
        Assertions.assertEquals(0, BigDecimal.valueOf(50).compareTo(portfolioState.getPositionStates().get(2).getMarketValue()));
        Assertions.assertEquals(1, portfolioState.getId());

        Assertions.assertEquals(3, portfolioState.getStockPriceStates().size());
        Assertions.assertEquals(0, BigDecimal.valueOf(100).compareTo(portfolioState.getStockPriceStates().get("AAPL").getCurrentPrice()));
        Assertions.assertNull(portfolioState.getStockPriceStates().get("AAPL").getPreviousPrice());
        Assertions.assertEquals(0, BigDecimal.valueOf(50).compareTo(portfolioState.getStockPriceStates().get("GOOGL").getCurrentPrice()));
        Assertions.assertNull(portfolioState.getStockPriceStates().get("GOOGL").getPreviousPrice());

        Assertions.assertEquals(0, BigDecimal.valueOf(1000).compareTo(portfolioState.getStockPriceStates().get("TESLA").getCurrentPrice()));
        Assertions.assertNull(portfolioState.getStockPriceStates().get("TESLA").getPreviousPrice());
    }

    @Test
    void compute_withNestedDerivatives() {
        when(portfolioService.getPortfolio()).thenReturn(new Portfolio(
                new ArrayList<Position>() {{
                    add(new Position("AAPL", 10));
                    add(new Position("GOOGL", 20));
                    add(new Position("TESLA_CALL_ON_CALL", 5));
                }}
        ));

        OptionDefinition nestedOptionDefinition = mock(OptionDefinition.class);
        BigDecimal nestedOptionPrice = new BigDecimal(40);

        when(instrumentService.getDefinition("TESLA_CALL_ON_CALL")).thenReturn(nestedOptionDefinition);
        when(nestedOptionDefinition.getUnderlyingSymbol()).thenReturn("TESLA_CALL");
        when(nestedOptionDefinition.getInstrumentType()).thenReturn(InstrumentType.OPTION);
        when(pricer.resolve(nestedOptionDefinition)).thenReturn(nestedOptionPrice);

        PortfolioState portfolioState = portfolioStateService.compute();
        Assertions.assertEquals(0, BigDecimal.valueOf(2200).compareTo(portfolioState.getPortfolioNav()));
        Assertions.assertEquals(3, portfolioState.getPositionStates().size());
        Assertions.assertEquals(0, BigDecimal.valueOf(1000).compareTo(portfolioState.getPositionStates().get(0).getMarketValue()));
        Assertions.assertEquals(0, BigDecimal.valueOf(1000).compareTo(portfolioState.getPositionStates().get(1).getMarketValue()));
        Assertions.assertEquals(0, BigDecimal.valueOf(200).compareTo(portfolioState.getPositionStates().get(2).getMarketValue()));
        Assertions.assertEquals(1, portfolioState.getId());

        Assertions.assertEquals(3, portfolioState.getStockPriceStates().size());
        Assertions.assertEquals(0, BigDecimal.valueOf(100).compareTo(portfolioState.getStockPriceStates().get("AAPL").getCurrentPrice()));
        Assertions.assertNull(portfolioState.getStockPriceStates().get("AAPL").getPreviousPrice());
        Assertions.assertEquals(0, BigDecimal.valueOf(50).compareTo(portfolioState.getStockPriceStates().get("GOOGL").getCurrentPrice()));
        Assertions.assertNull(portfolioState.getStockPriceStates().get("GOOGL").getPreviousPrice());

        Assertions.assertEquals(0, BigDecimal.valueOf(1000).compareTo(portfolioState.getStockPriceStates().get("TESLA").getCurrentPrice()));
        Assertions.assertNull(portfolioState.getStockPriceStates().get("TESLA").getPreviousPrice());
    }
}
