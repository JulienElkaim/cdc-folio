package com.crypto.folio.common.utils;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SystemClockImplTest {
    private final SystemClockImpl systemClockImpl = new SystemClockImpl();


    @Test
    void nowLocalDate() {
        assertEquals(LocalDate.now(), systemClockImpl.nowLocalDate());
    }

    @Test
    void nowInstant() throws InterruptedException {
        Instant before = Instant.now();
        Thread.sleep(1);
        Instant now = systemClockImpl.nowInstant();
        Thread.sleep(1);
        Instant after = Instant.now();
        assertTrue(before.isBefore(now));
        assertTrue(after.isAfter(now));
    }
}
