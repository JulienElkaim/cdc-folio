package com.crypto.market.data;

import java.math.BigDecimal;

public interface MarketDataProvider {
    BigDecimal getPriceAndSubscribe(String symbol, MarketDataListener listener);
}
