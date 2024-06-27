package com.crypto.folio.common.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

public interface SystemClock {
    LocalDate nowLocalDate();
    Instant nowInstant();
}
