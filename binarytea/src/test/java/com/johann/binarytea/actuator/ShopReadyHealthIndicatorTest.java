package com.johann.binarytea.actuator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.HealthContributorRegistry;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * <p>
 *
 * @author Johann
 * @version 1.0
 * @see
 **/
@SpringBootTest
public class ShopReadyHealthIndicatorTest {

    @Autowired
    private HealthContributorRegistry healthContributorRegistry;

    /**
     * 独立的ShopReadyHealthIndicatorTest,测试是否注册了shopReady
     */
    @Test
    void testRegistryContainsShopReady(){
        assertNotNull(healthContributorRegistry.getContributor("shopReady"));
    }
}
