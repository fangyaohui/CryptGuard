package com.example.crptguardusetest.config;

import com.crypt.cryptguard.config.CryptGuardAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @FileName CryptGuardConfig
 * @Description
 * @Author yaoHui
 * @date 2024-12-16
 **/
@Configuration
@Import(CryptGuardAutoConfiguration.class)
public class CryptGuardConfig {
}
