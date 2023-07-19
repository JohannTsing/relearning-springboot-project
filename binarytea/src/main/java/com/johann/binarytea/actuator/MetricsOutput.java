package com.johann.binarytea.actuator;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.core.instrument.logging.LoggingMeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 *
 * @author Johann
 * @version 1.0
 * @see
 **/
@Configuration
public class MetricsOutput {
    /**
     * 自定义MeterRegistry,将度量信息输出到日志中
     * @return
     */
    @Bean
    public MeterRegistry customMeterRegistry() {
        CompositeMeterRegistry meterRegistry = new CompositeMeterRegistry();
        meterRegistry.add(new SimpleMeterRegistry());
        meterRegistry.add(new LoggingMeterRegistry());
        return meterRegistry;
    }
}
