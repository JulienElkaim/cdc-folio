package com.crypto.market.data.mocked;

import java.math.BigDecimal;

@FunctionalInterface
public interface MarketPriceGenerator {
    BigDecimal generate(String symbol);
}
