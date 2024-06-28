package com.crypto.folio.core.portfolio.publish;

import com.crypto.folio.common.models.PortfolioState;
import com.crypto.folio.common.models.PositionState;
import com.crypto.folio.common.models.StockPriceState;
import com.crypto.folio.core.portfolio.publish.print.Printer;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

public class PortfolioStatePrintPublisherTest {
    private final Printer printer = mock(Printer.class);
    private final PortfolioStatePrintPublisher portfolioStatePrintPublisher = new PortfolioStatePrintPublisher(printer, 2);

    @Test
    void printInExpectedFormat_priceChanged() {
        PortfolioState portfolioState = new PortfolioState(
                43,
                new HashMap<String, StockPriceState>() {{
                    put("AAPL", new StockPriceState("AAPL", BigDecimal.TEN, BigDecimal.ONE));
                    put("GOOGL", new StockPriceState("GOOGL", BigDecimal.TEN, BigDecimal.TEN));
                }},
                new ArrayList<PositionState>() {{
                    add(new PositionState("CALL_AAPL", 40, BigDecimal.ONE, BigDecimal.valueOf(40)));
                    add(new PositionState("GOOGL", 50, BigDecimal.TEN, BigDecimal.valueOf(500)));
                }},
                BigDecimal.valueOf(12345.67)

        );

        portfolioStatePrintPublisher.publish(portfolioState);

        InOrder inOrder = Mockito.inOrder(printer);

        inOrder.verify(printer, times(3)).printWithFormat("%n");
        inOrder.verify(printer).printWithFormat("# 43 PORTFOLIO STATE %n");
        inOrder.verify(printer).printWithFormat("%n");
        inOrder.verify(printer).printWithFormat("## Market Data Updates %n");
        inOrder.verify(printer).printWithFormat("AAPL changed to 10%n");
        inOrder.verify(printer).printWithFormat("%n");
        inOrder.verify(printer).printWithFormat("## Portfolio %n");
        inOrder.verify(printer).printWithFormat("%-20s%20s%20s%20s %n", "symbol", "price", "quantity", "value");
        inOrder.verify(printer).printWithFormat("%-20s%20s%20s%20s %n", "CALL_AAPL", "1.00", "40.00", "40.00");
        inOrder.verify(printer).printWithFormat("%-20s%20s%20s%20s %n", "GOOGL", "10.00", "50.00", "500.00");
        inOrder.verify(printer).printWithFormat("%n");
        inOrder.verify(printer).printWithFormat("# Portfolio NAV: %63s", "12,345.67");
    }

    @Test
    void printInExpectedFormat_priceIsInitial() {
        PortfolioState portfolioState = new PortfolioState(
                43,
                new HashMap<String, StockPriceState>() {{
                    put("AAPL", new StockPriceState("AAPL", BigDecimal.TEN, null));
                    put("GOOGL", new StockPriceState("GOOGL", BigDecimal.TEN, BigDecimal.TEN));
                }},
                new ArrayList<PositionState>() {{
                    add(new PositionState("CALL_AAPL", 40, BigDecimal.ONE, BigDecimal.valueOf(40)));
                    add(new PositionState("GOOGL", 50, BigDecimal.TEN, BigDecimal.valueOf(500)));
                }},
                BigDecimal.valueOf(12345.67)

        );

        portfolioStatePrintPublisher.publish(portfolioState);

        InOrder inOrder = Mockito.inOrder(printer);

        inOrder.verify(printer, times(3)).printWithFormat("%n");
        inOrder.verify(printer).printWithFormat("# 43 PORTFOLIO STATE %n");
        inOrder.verify(printer).printWithFormat("%n");
        inOrder.verify(printer).printWithFormat("## Market Data Updates %n");
        inOrder.verify(printer).printWithFormat("AAPL initial price 10%n");
        inOrder.verify(printer).printWithFormat("%n");
        inOrder.verify(printer).printWithFormat("## Portfolio %n");
        inOrder.verify(printer).printWithFormat("%-20s%20s%20s%20s %n", "symbol", "price", "quantity", "value");
        inOrder.verify(printer).printWithFormat("%-20s%20s%20s%20s %n", "CALL_AAPL", "1.00", "40.00", "40.00");
        inOrder.verify(printer).printWithFormat("%-20s%20s%20s%20s %n", "GOOGL", "10.00", "50.00", "500.00");
        inOrder.verify(printer).printWithFormat("%n");
        inOrder.verify(printer).printWithFormat("# Portfolio NAV: %63s", "12,345.67");
    }
}
