package com.johann.binarytea.actuator;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 在Spring Security 5.7.1及更高版本中,WebSecurityConfigurerAdapter类已弃用，以支持基于组件的安全配置。
 * WebSecurityConfigurerAdapter类仍然存在，但它仅用于向后兼容性。
 * @author Johann
 * @version 1.0
 * @see
 **/
@Configuration
// 启用 Spring Security
@EnableWebSecurity
public class ActuatorSecurityConfigurerNew {

    /**
     *
     * @param http
     * @return
     * @throws Exception
     */
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http.authorizeRequests((requests) -> {
//            requests.antMatchers("/actuator/health", "/actuator/info").permitAll();
//            requests.anyRequest().authenticated();
//        }).httpBasic();
////        http.requestMatcher(EndpointRequest.toAnyEndpoint().excluding("health", "info"))
////                .authorizeRequests((requests) -> requests.anyRequest().authenticated());
////        http.httpBasic();
//        return http.build();
//    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.requestMatcher(EndpointRequest.toAnyEndpoint()).authorizeRequests((requests) -> {
            requests.anyRequest().anonymous();
        }).httpBasic();
        return http.build();
    }

}
