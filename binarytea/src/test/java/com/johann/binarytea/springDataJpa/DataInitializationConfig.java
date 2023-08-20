package com.johann.binarytea.springDataJpa;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
@ConditionalOnProperty(name = "when.test.jpa", havingValue = "true")
public class DataInitializationConfig {
    @Bean
    public DataInitializationRunner dataInitializationRunner() {
        return new DataInitializationRunner();
    }
}
