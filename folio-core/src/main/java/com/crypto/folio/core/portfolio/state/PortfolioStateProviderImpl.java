package com.crypto.folio.core.portfolio.state;

import com.crypto.folio.common.models.*;
import com.crypto.folio.core.portfolio.PortfolioService;
import com.crypto.folio.price.engine.PriceResolver;
import com.crypto.ref.data.InstrumentService;
import com.crypto.ref.data.models.InstrumentDefinition;
import com.crypto.ref.data.models.OptionDefinition;
import com.crypto.ref.data.models.StockDefinition;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PortfolioStateProviderImpl implements PortfolioStateProvider {
    private PortfolioState previousPortfolioState = null;
    private final PortfolioService portfolioService;
    private final InstrumentService instrumentService;
    private final PriceResolver<InstrumentDefinition> pricer;

    private final int decimalPrecision;

    public PortfolioStateProviderImpl(PortfolioService portfolioService,
                                      InstrumentService instrumentService,
                                      PriceResolver<InstrumentDefinition> pricer,
                                      int decimalPrecision) {
        this.portfolioService = portfolioService;
        this.instrumentService = instrumentService;
        this.pricer = pricer;
        this.decimalPrecision = decimalPrecision;
    }

    @Override
    public PortfolioState compute() {
        Portfolio portfolio = portfolioService.getPortfolio();

        int stateId;
        Map<String, StockPriceState> previousStockPriceStates;
        if (previousPortfolioState == null) {
            stateId = 1;
            previousStockPriceStates = new HashMap<>();
        } else {
            stateId = previousPortfolioState.getId() + 1;
            previousStockPriceStates = previousPortfolioState.getStockPriceStates();
        }

        List<PositionState> positionStates = new ArrayList<>();
        Map<String, StockPriceState> stockPriceStates = new HashMap<>();
        BigDecimal portfolioNav = BigDecimal.ZERO.setScale(decimalPrecision, RoundingMode.HALF_UP);

        for (Position position : portfolio.getPositions()) {
            String ticker = position.getTicker();
            InstrumentDefinition definition = instrumentService.getDefinition(ticker);

            if (definition == null) {
                throw new RuntimeException("Instrument not found: " + ticker);
            }

            BigDecimal positionPrice = computePrice(definition);

            String stockTicker;
            BigDecimal stockPrice;
            switch (definition.getInstrumentType()) {
                case STOCK:
                    stockTicker = ticker;
                    stockPrice = positionPrice;
                    break;
                case OPTION:
                    OptionDefinition optionDefinition = (OptionDefinition) definition;
                    StockDefinition nestedStockDefinition = resolveNestedStockDefinition(optionDefinition);
                    stockTicker = nestedStockDefinition.getSymbol();
                    stockPrice = computePrice(nestedStockDefinition);
                    break;
                default:
                    throw new RuntimeException("Unsupported instrument type: " + definition.getInstrumentType());
            }

            StockPriceState previousStockPriceState = previousStockPriceStates.get(stockTicker);
            BigDecimal previousPrice = previousStockPriceState == null ? null : previousStockPriceState.getCurrentPrice();

            stockPriceStates.put(stockTicker, new StockPriceState(stockTicker, stockPrice, previousPrice));

            BigDecimal marketValue = positionPrice.multiply(BigDecimal.valueOf(position.getQuantity()));
            positionStates.add(new PositionState(ticker, position.getQuantity(), positionPrice, marketValue));
            portfolioNav = portfolioNav.add(marketValue);
        }


        PortfolioState newPortfolioState = new PortfolioState(stateId, stockPriceStates, positionStates, portfolioNav);
        previousPortfolioState = newPortfolioState;
        return newPortfolioState;
    }

    private BigDecimal computePrice(InstrumentDefinition definition) {
        return pricer.resolve(definition).setScale(decimalPrecision, RoundingMode.HALF_UP);
    }

    private StockDefinition resolveNestedStockDefinition(OptionDefinition optionDefinition) {
        InstrumentDefinition underlyingDefinition = instrumentService.getDefinition(optionDefinition.getUnderlyingSymbol());
        if (underlyingDefinition == null) {
            throw new RuntimeException("Underlying instrument not found: " + optionDefinition.getUnderlyingSymbol());
        }
        if (underlyingDefinition instanceof StockDefinition) {
            return (StockDefinition) underlyingDefinition;

        } else if (underlyingDefinition instanceof OptionDefinition) {
            return resolveNestedStockDefinition((OptionDefinition) underlyingDefinition);

        } else {
            throw new RuntimeException("Unsupported instrument type: " + underlyingDefinition.getInstrumentType());
        }
    }
}
