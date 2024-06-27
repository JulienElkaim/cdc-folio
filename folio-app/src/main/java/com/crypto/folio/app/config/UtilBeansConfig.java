package com.crypto.folio.app.config;

import com.crypto.folio.common.utils.SystemClock;
import com.crypto.folio.common.utils.SystemClockImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UtilBeansConfig {
    @Bean
    public SystemClock systemClock() {
        return new SystemClockImpl();
    }
}
