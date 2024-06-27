package com.crypto.market.data.mocked;

import java.util.concurrent.ScheduledFuture;

public interface MockMarketPulsar {
    ScheduledFuture<?> runAtPulse(Runnable task);
}
