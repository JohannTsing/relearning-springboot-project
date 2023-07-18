package com.johann.binarytea.actuator;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.info.SimpleInfoContributor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 自定义Info端点
 *
 * @author Johann
 * @version 1.0
 * @see
 **/
@Configuration
public class MyInfoContributor {

    @Bean
    public SimpleInfoContributor simpleInfoContributor() {
        return new SimpleInfoContributor("simple","HelloWorld!");
    }
}
