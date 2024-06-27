package com.crypto.folio.core.portfolio.publish;

import com.crypto.folio.common.models.PortfolioState;
import com.crypto.folio.common.models.PositionState;
import com.crypto.folio.common.models.StockPriceState;
import com.crypto.folio.core.portfolio.publish.print.Printer;

import java.text.DecimalFormat;

public class PortfolioStatePrintPublisher implements PortfolioStatePublisher {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,##0.00");
    private static final int COLUMN_CHAR_SIZE = 20;
    private static final String lAlign = "%-" + COLUMN_CHAR_SIZE + "s";
    private static final String rAlign = "%" + COLUMN_CHAR_SIZE + "s";

    private final Printer printer;

    public PortfolioStatePrintPublisher(Printer printer) {
        this.printer = printer;
    }

    @Override
    public void publish(PortfolioState portfolioState) {
        breakLine();
        breakLine();
        breakLine();
        printer.printWithFormat("# " + portfolioState.getId() + " PORTFOLIO STATE %n");
        breakLine();
        printer.printWithFormat("## Market Data Updates %n");
        for (StockPriceState stockPriceState : portfolioState.getStockPriceStates().values()) {
            if (stockPriceState.getCurrentPrice() != null) {
                if (stockPriceState.getPreviousPrice() == null) {
                    printer.printWithFormat(stockPriceState.getSymbol() + " initial price " + stockPriceState.getCurrentPrice() + "%n");
                    continue;
                } else if (stockPriceState.getPreviousPrice().compareTo(stockPriceState.getCurrentPrice()) != 0) {
                    printer.printWithFormat(stockPriceState.getSymbol() + " changed to " + stockPriceState.getCurrentPrice() + "%n");
                }
                ;
            }
        }
        breakLine();
        printer.printWithFormat("## Portfolio %n");
        Object[] columnNames = {"symbol", "price", "quantity", "value"};


        printer.printWithFormat(lAlign + rAlign + rAlign + rAlign + " %n", columnNames);

        for (PositionState positionState : portfolioState.getPositionStates()) {
            printer.printWithFormat(lAlign + rAlign + rAlign + rAlign + " %n",
                    positionState.getSymbol(),
                    DECIMAL_FORMAT.format(positionState.getPrice()),
                    DECIMAL_FORMAT.format(positionState.getQuantity()),
                    DECIMAL_FORMAT.format(positionState.getMarketValue())
            );
        }
        breakLine();
        String totalPrefix = "# Portfolio NAV: ";
        String alignRightSpaces = "%" + (COLUMN_CHAR_SIZE * columnNames.length - totalPrefix.length()) + "s";
        printer.printWithFormat(totalPrefix + alignRightSpaces,
                DECIMAL_FORMAT.format(portfolioState.getPortfolioNav()));
    }

    private void breakLine() {
        printer.printWithFormat("%n");
    }

    @Override
    public void publishError(String errorMessage) {
        printer.printWithFormat("## Something bad happened when checking Portfolio State: " + errorMessage + "%n");
    }
}
