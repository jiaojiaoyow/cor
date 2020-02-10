package com.exa.user.config;

import com.exa.user.interceptor.Jwtinterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;


@Configuration
public class InterceptorConfig extends WebMvcConfigurationSupport {

    @Autowired
    private Jwtinterceptor jwtinterceptor;

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        //注册拦截器要声明拦截器对象和拦截的请求
        registry.addInterceptor(jwtinterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/**/login/**");
    }
}
