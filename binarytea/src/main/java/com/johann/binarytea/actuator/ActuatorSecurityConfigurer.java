package com.johann.binarytea.actuator;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * 在Spring Security 5.7.1及更高版本中,WebSecurityConfigurerAdapter类已弃用，以支持基于组件的安全配置。
 *
 * @author Johann
 * @version 1.0
 * @see
 **/
//@Configuration
@Deprecated
public class ActuatorSecurityConfigurer extends WebSecurityConfigurerAdapter {

    /** 不用登录页，而是使用HTTP Basic 的方式进行验证(但 health,info 端点除外)
     * requestMatcher方法：用于匹配除health和info端点以外的所有Actuator端点。
     * authorizeRequests方法：用于指定对Actuator端点的所有请求必须经过身份验证。
     * httpBasic方法：用于配置Spring Security使用HTTP Basic身份验证。
     * @param http
     * @throws Exception
     */
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.requestMatcher(EndpointRequest.toAnyEndpoint().excluding("health", "info"))
//                .authorizeRequests((requests) -> requests.anyRequest().authenticated());
//        http.httpBasic();
//    }

    /** 针对所有端点，可以提供匿名访问
     * requestMatcher方法：用于匹配所有Actuator端点。
     * authorizeRequests方法：用于指定允许匿名访问Actuator端点的所有请求。
     * httpBasic方法：用于配置Spring Security使用HTTP Basic身份验证。
     * anonymous()方法：用于覆盖默认行为，允许匿名访问所有Actuator端点。
     * @param http
     * @throws Exception
     */
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.requestMatcher(EndpointRequest.toAnyEndpoint()).authorizeRequests((requests) -> {
//            requests.anyRequest().anonymous();
//        }).httpBasic();
//    }
}
