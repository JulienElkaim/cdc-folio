package com.crypto.folio.common.models;

import java.util.Collections;
import java.util.List;

public class Portfolio {
    private final List<Position> items;

    public Portfolio(List<Position> items) {
        this.items = items;
    }

    public List<Position> getPositions() {
        return Collections.unmodifiableList(items);
    }

    @Override
    public String toString() {
        return "Portfolio{" +
                "items=" + items +
                '}';
    }
}
