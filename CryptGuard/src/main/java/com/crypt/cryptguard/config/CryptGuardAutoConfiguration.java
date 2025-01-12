package com.crypt.cryptguard.config;

import com.crypt.cryptguard.Interceptor.EncryptQueryInterceptor;
import com.crypt.cryptguard.Interceptor.EncryptUpdateInterceptor;
import com.crypt.cryptguard.aspect.DecryptRequestAspect;
import com.crypt.cryptguard.aspect.EncryptResponseAspect;
import com.crypt.cryptguard.filter.RequestCachingFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @FileName CryptGuardAutoConfiguration
 * @Description
 * @Author yaoHui
 * @date 2024-12-16
 **/
@Slf4j
@Configuration
public class CryptGuardAutoConfiguration {

    @Bean
    public DecryptRequestAspect decryptRequestAspect(){
        log.info("decryptRequestAspect is running");
        return new DecryptRequestAspect();
    }

    @Bean
    public EncryptResponseAspect encryptResponseAspect(){
        log.info("encryptResponseAspect is running");
        return new EncryptResponseAspect();
    }

    @Bean
    public FilterRegistrationBean<RequestCachingFilter> requestCachingFilterFilterRegistrationBean(){
        FilterRegistrationBean<RequestCachingFilter> requestCachingFilterFilterRegistrationBean =
                new FilterRegistrationBean<>();
        requestCachingFilterFilterRegistrationBean.setFilter(new RequestCachingFilter());
        requestCachingFilterFilterRegistrationBean.addUrlPatterns("/*");
        return requestCachingFilterFilterRegistrationBean;
    }

    @Bean
    public WebConfig webConfig(){
        log.info("WebConfig");
        return new WebConfig();
    }

    @Bean
    public EncryptUpdateInterceptor encryptInterceptor(){
        log.info("EncryptInterceptor init bean is running...");
        return new EncryptUpdateInterceptor();
    }

    @Bean
    public EncryptQueryInterceptor encryptQueryInterceptor(){
        log.info("EncryptQueryInterceptor is running");
        return new EncryptQueryInterceptor();
    }
}
