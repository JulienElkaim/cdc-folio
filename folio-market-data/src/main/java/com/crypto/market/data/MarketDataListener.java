package com.crypto.market.data;

import java.math.BigDecimal;

public interface MarketDataListener {
    void onPriceChange(String symbol, BigDecimal newPrice);
}
