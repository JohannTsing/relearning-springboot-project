package com.johann.binarytea.config;

import com.johann.binarytea.BinaryteaApplication;
import com.johann.binarytea.actuator.ShopReadyHealthIndicator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <p>
 *
 * @author Johann
 * @version 1.0
 * @see
 **/
@SpringBootTest(classes = BinaryteaApplication.class,
        properties = {"binarytea.ready=false","binarytea.openHours=8:00-18:00"})
public class ShopConfigurationDisableTest {
    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void testPropertiesBeanUnavailable() {
        assertEquals("false",
                applicationContext.getEnvironment().getProperty("binarytea.ready"));
        assertFalse(applicationContext
                .containsBean("binarytea-com.johann.binarytea.BinaryTeaProperties"));
    }

    @Test
    void testIndicatorDown() {
        ShopReadyHealthIndicator indicator = applicationContext.getBean(ShopReadyHealthIndicator.class);
        assertEquals(Status.DOWN, indicator.getHealth(false).getStatus());
    }
}
