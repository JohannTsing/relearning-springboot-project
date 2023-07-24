package com.johann.config;

import com.johann.binarytea.BinaryTeaProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 为了演示自动配置的加载，故意不放在com.johann.binarytea包里（将 @SpringBootApplication的扫描路径设置为以下路径(scanBasePackages = "com.johann.binarytea")）
 * 必须在添加 spring.factories 中的 以下配置，SpringBoot才会自动加载配置该类
 * org.springframework.boot.autoconfigure.EnableAutoConfiguration=com.johann.config.ShopConfiguration
 *
 * @author Johann
 * @version 1.0
 * @see
 **/
@Configuration
@EnableConfigurationProperties(BinaryTeaProperties.class)
@ConditionalOnProperty(name = "binarytea.ready", havingValue = "true")
public class ShopConfiguration {
}
