package com.exa.user.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        /**
         * authorizeRequests所有security全注解配置实现得开端，表示开始说明需要的权限
         * 需要的权限：1. 拦截的路径 2. 访问路径需要的权限
         * antMatchers("/**")表示路径.permitAll()表示任何权限都可以
         * anyRequest()任何请求
         */
        http
                .authorizeRequests()
                .antMatchers("/**").permitAll() //配置所有的请求都可以匿名访问
                .anyRequest().authenticated()
                .and().csrf().disable();
    }


}
