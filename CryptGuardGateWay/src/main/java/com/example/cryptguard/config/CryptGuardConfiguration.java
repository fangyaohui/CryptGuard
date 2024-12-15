package com.example.cryptguard.config;

import com.example.cryptguard.filter.RequestDecryptFilter;
import com.example.cryptguard.filter.ResponseEncryptFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @FileName CryptGuardProperties
 * @Description
 * @Author yaoHui
 * @date 2024-12-14
 **/
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "custom.crypt", name = "enable", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(CryptGuardProperties.class) // 使 CryptGuardProperties 自动绑定
public class CryptGuardConfiguration {

    private final CryptGuardProperties cryptGuardProperties;

    public CryptGuardConfiguration(CryptGuardProperties cryptGuardProperties) {
        this.cryptGuardProperties = cryptGuardProperties;
    }

    @Bean
    public RequestDecryptFilter requestDecryptFilter(){
        log.info("CryptGuardConfiguration RequestDecryptFilter is running");
        log.info("CryptGuardProperties is :" + cryptGuardProperties.toString());
        return new RequestDecryptFilter(cryptGuardProperties);
    }

    @Bean
    public ResponseEncryptFilter responseEncryptFilter(){
        log.info("CryptGuardConfiguration ResponseEncryptFilter is running");
        log.info("CryptGuardProperties is :" + cryptGuardProperties.toString());
        return new ResponseEncryptFilter(cryptGuardProperties);
    }

}
