package com.johann.binarytea.config;

import com.johann.binarytea.BinaryTeaProperties;
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
        properties = {"binarytea.ready=true","binarytea.openHours=8:00-18:00"})
public class ShopConfigurationEnableTest {
    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void testPropertiesBeanAvailable() {
        assertNotNull(applicationContext.getBean(BinaryTeaProperties.class));
        assertTrue(applicationContext
                .containsBean("binarytea-com.johann.binarytea.BinaryTeaProperties"));
    }

    @Test
    void testPropertyValues() {
        BinaryTeaProperties properties =
                applicationContext.getBean(BinaryTeaProperties.class);
        assertTrue(properties.isReady());
        assertEquals("8:00-18:00", properties.getOpenHours());
    }

    @Test
    void testIndicatorUp() {
        ShopReadyHealthIndicator indicator = applicationContext.getBean(ShopReadyHealthIndicator.class);
        assertEquals(Status.UP, indicator.getHealth(false).getStatus());
    }
}
