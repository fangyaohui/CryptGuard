package com.example.gateway.config;

import com.example.cryptguard.config.CryptGuardConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @FileName CryptGuardConfig
 * @Description
 * @Author yaoHui
 * @date 2024-12-09
 **/
@Slf4j
@Configuration
@Import(CryptGuardConfiguration.class)
public class CryptGuardConfig {

}
