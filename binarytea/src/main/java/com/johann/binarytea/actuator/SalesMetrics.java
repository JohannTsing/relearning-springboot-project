package com.johann.binarytea.actuator;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 自定义度量指标，监控经营指标
 *
 * @author Johann
 * @version 1.0
 * @see
 **/
@Component
public class SalesMetrics implements MeterBinder {

    private Counter orderCount;
    private Counter orderAmount;
    private DistributionSummary orderSummary;
    private AtomicInteger averageAmount = new AtomicInteger();


    /**
     * 绑定度量指标
     * @param meterRegistry
     */
    @Override
    public void bindTo(MeterRegistry meterRegistry) {
        this.orderCount = meterRegistry.counter("order.count","direction","income");
        this.orderAmount = meterRegistry.counter("order.amount","direction","income");
        this.orderSummary = meterRegistry.summary("order.summary","direction","income");
        meterRegistry.gauge("order.average.amount",averageAmount);
    }

    /**
     * 创建新订单
     * @param amount
     */
    public void makeNewOrder(int amount){
        orderCount.increment();
        orderAmount.increment(amount);
        orderSummary.record(amount);
        averageAmount.set((int) orderSummary.mean());
    }
}
