package com.example.gateway.config;

import com.example.cryptguard.filter.RequestDecryptFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @FileName CryptGuardConfig
 * @Description
 * @Author yaoHui
 * @date 2024-12-04
 **/
@Configuration
public class CryptGuardConfig {


    @Bean
    public FilterRegistrationBean<RequestDecryptFilter> requestDecryptFilterFilterRegistrationBean(){
        FilterRegistrationBean<RequestDecryptFilter> requestDecryptFilterFilterRegistrationBean =
                new FilterRegistrationBean<>();
        requestDecryptFilterFilterRegistrationBean.setFilter(new RequestDecryptFilter());
        requestDecryptFilterFilterRegistrationBean.addUrlPatterns("/*");
        return requestDecryptFilterFilterRegistrationBean;
    }
}
