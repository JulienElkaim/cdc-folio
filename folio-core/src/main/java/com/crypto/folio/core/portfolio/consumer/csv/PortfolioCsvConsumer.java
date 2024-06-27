package com.crypto.folio.core.portfolio.consumer.csv;

import com.crypto.folio.common.models.Portfolio;
import com.crypto.folio.common.models.Position;
import com.crypto.folio.core.portfolio.consumer.PortfolioConsumer;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PortfolioCsvConsumer implements PortfolioConsumer {
    private final String resourceRelativePath;
    private final CsvReader csvReader;

    public PortfolioCsvConsumer(String resourceRelativePath,
                                CsvReader csvReader) {
        this.resourceRelativePath = resourceRelativePath;
        this.csvReader = csvReader;
    }

    @Override
    public Portfolio consume() {
        try (BufferedReader br = csvReader.read(resourceRelativePath)) {
            List<Position> positions = new ArrayList<>();
            String line;


            br.readLine(); // Just skipping the header line
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                String ticker = values[0];
                int quantity = Integer.parseInt(values[1]);
                Position item = new Position(ticker, quantity);
                positions.add(item);
            }
            return new Portfolio(positions);
        } catch (IOException e) {
            throw new RuntimeException("I/O error: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error while parsing the CSV file", e);
        }
    }
}
