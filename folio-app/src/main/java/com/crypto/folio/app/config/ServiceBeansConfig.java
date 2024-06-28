package com.crypto.folio.app.config;

import com.crypto.folio.app.watcher.PortfolioStateWatcher;
import com.crypto.folio.common.models.Portfolio;
import com.crypto.folio.common.utils.SystemClock;
import com.crypto.folio.core.portfolio.PortfolioService;
import com.crypto.folio.core.portfolio.PortfolioServiceImpl;
import com.crypto.folio.core.portfolio.PortfolioStore;
import com.crypto.folio.core.portfolio.consumer.PortfolioConsumer;
import com.crypto.folio.core.portfolio.consumer.csv.CsvReader;
import com.crypto.folio.core.portfolio.consumer.csv.PortfolioCsvConsumer;
import com.crypto.folio.core.portfolio.consumer.csv.ResourcesCsvReader;
import com.crypto.folio.core.portfolio.publish.PortfolioStatePrintPublisher;
import com.crypto.folio.core.portfolio.publish.PortfolioStatePublisher;
import com.crypto.folio.core.portfolio.publish.print.ConsolePrinter;
import com.crypto.folio.core.portfolio.publish.print.Printer;
import com.crypto.folio.core.portfolio.state.PortfolioStateProvider;
import com.crypto.folio.core.portfolio.state.PortfolioStateProviderImpl;
import com.crypto.folio.price.engine.PriceEngine;
import com.crypto.folio.price.engine.PriceResolver;
import com.crypto.folio.price.engine.impl.OptionPriceResolver;
import com.crypto.folio.price.engine.impl.PriceEngineImpl;
import com.crypto.folio.price.engine.impl.StockPriceResolver;
import com.crypto.market.data.MarketDataProvider;
import com.crypto.market.data.MarketDataService;
import com.crypto.market.data.MarketDataServiceImpl;
import com.crypto.market.data.mocked.*;
import com.crypto.ref.data.InstrumentService;
import com.crypto.ref.data.InstrumentStore;
import com.crypto.ref.data.impl.InstrumentServiceImpl;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.RealDistribution;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Configuration
public class ServiceBeansConfig {
    @Configuration
    public static class PortfolioConfig {
        @Bean
        public CsvReader resourcesCsvReader() {
            return new ResourcesCsvReader();
        }

        @Bean
        public PortfolioConsumer portfolioCsvConsumer(@Value("${portfolio.csv.resource.path}") String path,
                                                      CsvReader csvReader) {
            return new PortfolioCsvConsumer(path, csvReader);
        }

        @Bean
        public PortfolioService portfolioService(PortfolioConsumer consumer,
                                                 PortfolioStore portfolioStore) {
            Portfolio valueAtStart = consumer.consume();
            return new PortfolioServiceImpl(valueAtStart, portfolioStore);
        }
    }

    @Configuration
    public static class PortfolioStateConfig {
        @Bean
        public PortfolioStateWatcher portfolioStateWatcher(PortfolioStateProvider portfolioStateProvider,
                                                           PortfolioStatePublisher publisher) {
            return new PortfolioStateWatcher(portfolioStateProvider, publisher);
        }

        @Bean
        public PortfolioStateProvider portfolioStateService(PortfolioService portfolioService,
                                                            InstrumentService instrumentService,
                                                            PriceEngine priceEngine,
                                                            @Value("${portfolio.figures.decimal.precision}") int decimalPrecision) {
            return new PortfolioStateProviderImpl(
                    portfolioService,
                    instrumentService,
                    priceEngine,
                    decimalPrecision);
        }

        @Bean
        public PortfolioStatePublisher portfolioStatePublisher(Printer printer,
                                                               @Value("${portfolio.figures.decimal.precision}") int decimalPrecision) {
            return new PortfolioStatePrintPublisher(printer, decimalPrecision);
        }

        @Bean
        public Printer consolePrinter() {
            return new ConsolePrinter();
        }
    }


    @Configuration
    public static class PriceConfig {

        @Bean
        RealDistribution normalDistribution() {
            return new NormalDistribution(); // Default constructor is the standardized one, 0,1
        }

        @Bean
        List<PriceResolver<?>> priceResolvers(MarketDataService marketDataService,
                                              InstrumentService instrumentService,
                                              SystemClock systemClock,
                                              RealDistribution realDistribution,
                                              @Value("${reference.risk.free.rate}") double riskFree) {
            StockPriceResolver stockPriceResolver = new StockPriceResolver(marketDataService);
            return new ArrayList<PriceResolver<?>>() {{
                add(stockPriceResolver);
                add(new OptionPriceResolver(stockPriceResolver, instrumentService, systemClock, realDistribution, riskFree));
            }};
        }

        @Bean
        PriceEngine priceEngine(List<PriceResolver<?>> priceResolvers) {
            return new PriceEngineImpl(priceResolvers.stream()
                    .collect(Collectors.toMap(PriceResolver::getSupportedClass, pr -> pr)));
        }
    }

    @Configuration
    public static class MarketDataConfig {

        @Bean
        MarketDataService marketDataService(MarketDataProvider marketDataProvider) {
            return new MarketDataServiceImpl(marketDataProvider);
        }

        @Bean
        MarketDataProvider marketDataProvider(MarketPriceGenerator marketPriceGenerator,
                                              MockMarketPulsar mockMarketPulsar) {
            return new MockedMarketDataProvider(mockMarketPulsar,
                    marketPriceGenerator,
                    Collections.EMPTY_MAP);
        }

        @Bean
        MockMarketPulsar mockScheduler(@Value("${market.pulsar.frequency.ms}") int frequencyMs) {
            ScheduledExecutorService schedulers = Executors.newScheduledThreadPool(4);
            return (task) -> schedulers.scheduleAtFixedRate(task, frequencyMs, frequencyMs, TimeUnit.MILLISECONDS);

        }

        @Bean
        MarketPriceGenerator priceGenerator(InstrumentService instrumentService,
                                            SystemClock systemClock) {
            Instant timeReference = Instant.now();
            Map<String, StockPriceSnapshot> initialPricesMap = new HashMap<String, StockPriceSnapshot>() {{
                put("APPL", new StockPriceSnapshot("AAPL", BigDecimal.valueOf(209.35), timeReference));
                put("TESLA", new StockPriceSnapshot("TESLA", BigDecimal.valueOf(185.52), timeReference));
            }};

            return new BrownianMotionMarketPriceMocker(
                    instrumentService,
                    systemClock,
                    initialPricesMap,
                    BigDecimal.valueOf(130),
                    timeReference,
                    new Random()
            );
        }
    }

    @Configuration
    public static class RefDataConfig {
        @Bean
        public InstrumentService instrumentService(InstrumentStore instrumentStore) {
            return new InstrumentServiceImpl(instrumentStore);
        }
    }


}
