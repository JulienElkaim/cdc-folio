package com.crypto.ref.data.models;

import com.crypto.folio.common.models.InstrumentType;
import com.crypto.folio.common.models.OptionType;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class OptionDefinition implements InstrumentDefinition {
    @Id
    private String symbol;
    private String underlyingSymbol;
    private LocalDate expiryDate;
    private BigDecimal strikePrice;
    private OptionType optionType;

    public OptionDefinition(){}

    public OptionDefinition(String symbol, String underlyingSymbol, LocalDate expiryDate, BigDecimal strikePrice, OptionType optionType) {
        this.symbol = symbol;
        this.underlyingSymbol = underlyingSymbol;
        this.expiryDate = expiryDate;
        this.strikePrice = strikePrice;
        this.optionType = optionType;
    }

    @Override
    public String getSymbol() {
        return symbol;
    }

    @Override
    public InstrumentType getInstrumentType() {
        return InstrumentType.OPTION;
    }

    public String getUnderlyingSymbol() {
        return underlyingSymbol;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public BigDecimal getStrikePrice() {
        return strikePrice;
    }

    public OptionType getOptionType() {
        return optionType;
    }

    @Override
    public String toString() {
        return "OptionDefinition{" +
                "symbol='" + symbol + '\'' +
                ", underlyingSymbol='" + underlyingSymbol + '\'' +
                ", expiryDate=" + expiryDate +
                ", strikePrice=" + strikePrice +
                ", optionType=" + optionType +
                '}';
    }
}
