package com.example.gateway.config;

import com.example.cryptguard.filter.RequestDecryptFilter;
import com.example.cryptguard.filter.ResponseEncryptFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @FileName CryptGuardConfig
 * @Description
 * @Author yaoHui
 * @date 2024-12-09
 **/
@Configuration
public class CryptGuardConfig {

    @Bean
    public RequestDecryptFilter requestDecryptFilter(){
        return new RequestDecryptFilter();
    }

    @Bean
    public ResponseEncryptFilter responseEncryptFilter(){
        return new ResponseEncryptFilter();
    }
}
