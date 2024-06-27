package com.crypto.folio.common.utils;

import java.time.Instant;
import java.time.LocalDate;

public class SystemClockImpl implements SystemClock {
    @Override
    public LocalDate nowLocalDate() {
        return LocalDate.now();
    }

    @Override
    public Instant nowInstant() {
        return Instant.now();
    }
}
