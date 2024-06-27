package com.crypto.folio.core.portfolio.publish.print;

public class ConsolePrinter implements Printer {
    @Override
    public void printWithFormat(String format, Object... args) {
        System.out.printf(format, args);
    }
}
