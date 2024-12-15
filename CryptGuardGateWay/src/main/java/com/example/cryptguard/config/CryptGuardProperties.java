package com.example.cryptguard.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @FileName CryptGuardProperties
 * @Description
 * @Author yaoHui
 * @date 2024-12-14
 **/
@Data
@Component
@ConfigurationProperties(prefix = "custom.crypt")
public class CryptGuardProperties {

    // 控制是否启用配置
    private boolean enable = false;

    // 需要进行解密的API-URL列表参数
    private List<String> deCryptUrls = new ArrayList<>();

    // 需要进行加密的API-URL列表参数
    private List<String> enCryptUrls = new ArrayList<>();

    // 设置的私钥
    private String privateKey = null;

}
