package com.crypt.cryptguard.config;

import com.crypt.cryptguard.resolver.DecryptArgumentResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @FileName WebConfig
 * @Description
 * @Author yaoHui
 * @date 2024-12-19
 **/
@Slf4j
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    public DecryptArgumentResolver decryptArgumentResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        log.info("addArgumentResolvers is running");
//        DecryptArgumentResolver decryptArgumentResolver = new DecryptArgumentResolver();
        resolvers.add(decryptArgumentResolver);
        log.info("Registered Argument Resolvers: " + resolvers);
    }
}
