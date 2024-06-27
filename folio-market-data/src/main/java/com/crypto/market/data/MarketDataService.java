package com.crypto.market.data;

import java.math.BigDecimal;

public interface MarketDataService {
    BigDecimal getPrice(String symbol);

}
