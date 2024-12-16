package com.crypt.cryptguard.config;

import com.crypt.cryptguard.aspect.DecryptRequestAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @FileName CryptGuardAutoConfiguration
 * @Description
 * @Author yaoHui
 * @date 2024-12-16
 **/
@Slf4j
@Configuration
//@EnableAspectJAutoProxy
public class CryptGuardAutoConfiguration {

    @Bean
    public DecryptRequestAspect decryptRequestAspect(){
        log.info("decryptRequestAspect is running");
        return new DecryptRequestAspect();
    }
}
