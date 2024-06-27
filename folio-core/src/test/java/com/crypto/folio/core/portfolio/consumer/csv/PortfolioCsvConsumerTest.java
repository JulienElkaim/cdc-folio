package com.crypto.folio.core.portfolio.consumer.csv;

import com.crypto.folio.common.models.Portfolio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PortfolioCsvConsumerTest {
    private static final String RESOURCE_PATH = "portfolio.csv";
    private final CsvReader csvReader = mock(CsvReader.class);
    private final PortfolioCsvConsumer portfolioCsvConsumer = new PortfolioCsvConsumer(RESOURCE_PATH, csvReader);
    private final BufferedReader bufferedReader = mock(BufferedReader.class);

    @BeforeEach
    void init() throws IOException {
        when(bufferedReader.readLine()).thenReturn("symbol,qty", "BTC,100", "ETH,200", null);
        when(csvReader.read(RESOURCE_PATH)).thenReturn(bufferedReader);
    }

    @Test
    void consume() {
        Portfolio portfolio = portfolioCsvConsumer.consume();

        assertNotNull(portfolio);
        assertNotNull(portfolio.getPositions());
        assertEquals(2, portfolio.getPositions().size());
        assertEquals("BTC", portfolio.getPositions().get(0).getTicker());
        assertEquals(100, portfolio.getPositions().get(0).getQuantity());
        assertEquals("ETH", portfolio.getPositions().get(1).getTicker());
        assertEquals(200, portfolio.getPositions().get(1).getQuantity());
    }

    @Test
    void consume_anythingHappenDuringConsumption_throw() throws IOException {
        when(bufferedReader.readLine()).thenReturn("symbol,qty", "BTC,100", "ETH,NOT_AN_INTEGER_OOPS", null);
        assertThrows(RuntimeException.class, portfolioCsvConsumer::consume);
    }

    @Test
    void consume_bufferedReaderIOException() throws IOException {
        when(bufferedReader.readLine()).thenThrow(new IOException("Wow!"));

        RuntimeException runtimeException = assertThrows(RuntimeException.class, portfolioCsvConsumer::consume);
        assertEquals("I/O error: Wow!", runtimeException.getMessage());
    }

    @Test
    void consume_noLines_emptyPortfolio() throws IOException {
        when(bufferedReader.readLine()).thenReturn(null);
        Portfolio portfolio = portfolioCsvConsumer.consume();

        assertNotNull(portfolio);
        assertNotNull(portfolio.getPositions());
        assertEquals(0, portfolio.getPositions().size());
    }
}
