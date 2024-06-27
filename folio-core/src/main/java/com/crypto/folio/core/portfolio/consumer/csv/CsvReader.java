package com.crypto.folio.core.portfolio.consumer.csv;

import java.io.BufferedReader;

public interface CsvReader {

    BufferedReader read(String path);
}
