package com.jn.audit.examples.springmvcdemo.common.config;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.YieldingWaitStrategy;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({AuditProperties.class})
public class AuditConfig {


    @Bean("blocking")
    public BlockingWaitStrategy blockingWaitStrategy() {
        return new BlockingWaitStrategy();
    }

    @Bean("sleeping")
    public SleepingWaitStrategy sleepingWaitStrategy() {
        return new SleepingWaitStrategy();
    }

    @Bean("yielding")
    public YieldingWaitStrategy yieldingWaitStrategy() {
        return new YieldingWaitStrategy();
    }

    @Bean("busySpin")
    public BusySpinWaitStrategy busySpinWaitStrategy() {
        return new BusySpinWaitStrategy();
    }


}
