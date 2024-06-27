package com.crypto.ref.data.models;

import com.crypto.folio.common.models.InstrumentType;

import javax.persistence.Entity;
import javax.persistence.Id;


@Entity
public class StockDefinition implements InstrumentDefinition {
    @Id
    private String symbol;
    private double expectedReturn;
    private double annualizedStd;

    public StockDefinition(String symbol, double expectedReturn, double annualizedStd) {
        this.symbol = symbol;
        this.expectedReturn = expectedReturn;
        this.annualizedStd = annualizedStd;
    }

    public StockDefinition() {
    }


    @Override
    public String getSymbol() {
        return symbol;
    }

    public double getExpectedReturn() {
        return expectedReturn;
    }

    public double getAnnualizedStd() {
        return annualizedStd;
    }

    @Override
    public InstrumentType getInstrumentType() {
        return InstrumentType.STOCK;
    }

    @Override
    public String toString() {
        return "StockDefinition{" +
                "symbol='" + symbol + '\'' +
                ", expectedReturn=" + expectedReturn +
                ", annualizedStd=" + annualizedStd +
                '}';
    }
}
