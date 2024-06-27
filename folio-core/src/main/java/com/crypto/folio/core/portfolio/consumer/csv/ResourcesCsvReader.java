package com.crypto.folio.core.portfolio.consumer.csv;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ResourcesCsvReader implements CsvReader {
    @Override
    public BufferedReader read(String path) {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(path);
        if (inputStream == null) {
            throw new RuntimeException("Resource not found: " + path);
        }
        return new BufferedReader(new InputStreamReader(inputStream));
    }
}
