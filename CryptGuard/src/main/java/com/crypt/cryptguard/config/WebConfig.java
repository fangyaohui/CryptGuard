package com.crypt.cryptguard.config;

import com.crypt.cryptguard.resolver.CustomMappingJackson2HttpMessageConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageConverter;
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

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 创建自定义的消息转换器
        CustomMappingJackson2HttpMessageConverter customConverter = new CustomMappingJackson2HttpMessageConverter();

        // 将自定义转换器添加到 Spring 的消息转换器列表中
        converters.add(0,customConverter);
    }
}
