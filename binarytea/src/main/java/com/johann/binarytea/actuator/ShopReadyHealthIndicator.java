package com.johann.binarytea.actuator;

import com.johann.binarytea.BinaryTeaProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;

/**
 * 自定义HealthIndicator
 * `HealthIndicator` 的实现类必须是 Spring Bean，否则不会被注册到 `HealthEndpoint` 中。
 * @author Johann
 * @version 1.0
 * @see
 **/
@Component
public class ShopReadyHealthIndicator extends AbstractHealthIndicator {

    private BinaryTeaProperties binaryTeaProperties;

    /**
     * 构造函数注入BinaryTeaProperties
     * 此处之所以使用ObjectProvider，是因为BinaryTeaProperties可能还没有被创建
     * @param binaryTeaProperties
     */
    public ShopReadyHealthIndicator(ObjectProvider<BinaryTeaProperties> binaryTeaProperties) {
        this.binaryTeaProperties = binaryTeaProperties.getIfAvailable();
    }

    /**
     * 实现doHealthCheck方法，用于检查应用程序的状态。
     * @param builder the {@link Builder} to report health status and details
     */
    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception{
        if (binaryTeaProperties==null || !binaryTeaProperties.isReady()) {
            builder.down().withDetail("message", "Shop is not ready!");
        } else {
            builder.up().withDetail("message", "Shop is ready!");
        }
    }
}
