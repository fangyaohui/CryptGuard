package com.example.crptguardusetest.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @FileName MappingJackson2HttpMessageConverter
 * @Description
 * @Author yaoHui
 * @date 2024-12-19
 **/
//@Component
@Slf4j
public class CustomMappingJackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {

    @Override
    public Object read(Type type, Class<?> contextClass, HttpInputMessage inputMessage) throws IOException {
        // 读取 JSON 数据
        // 通过 getBody() 获取 InputStream
        InputStream inputStream = inputMessage.getBody();

        // 使用 StreamUtils.copyToString 来读取 InputStream 并转换为 String
        String body = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
//        String json = StreamUtils.copyToString(inputMessage.getBody(), Objects.requireNonNull(getDefaultCharset()));

        // 使用 Jackson 的 ObjectMapper 解析 JSON
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(body);

        // 自定义逻辑: 如果 id 字段是字符串，则将其设置为 null
        if (rootNode.has("id") && rootNode.get("id").isTextual()) {
            ((ObjectNode) rootNode).putNull("id");
        }

        // 使用默认的转换逻辑
        return super.read(type, contextClass, inputMessage);
    }
}
