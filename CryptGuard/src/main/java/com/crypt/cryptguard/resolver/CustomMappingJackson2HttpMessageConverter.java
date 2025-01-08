package com.crypt.cryptguard.resolver;

import com.crypt.cryptguard.annotation.CryptController;
import com.crypt.cryptguard.annotation.DecryptController;
import com.crypt.cryptguard.annotation.DecryptRequest;
import com.crypt.cryptguard.utils.JSONProcessorUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

/**
 * 自定义 MappingJackson2HttpMessageConverter 实现，用于支持解密注解的请求处理。
 *
 * 功能：
 * 1. 检查当前处理的请求是否需要解密（通过 @DecryptRequest 注解判断）。
 * 2. 如果需要解密，解析 JSON 请求体，并按需移除指定字段或进行其他解密逻辑。
 * 3. 支持 Spring 的消息转换器机制，将修改后的 JSON 交由父类进行常规转换。
 *
 * 注意事项：
 * - InputStream 是一次性的，一旦读取后需重新包装以避免数据丢失。
 * - 通过反射解析目标类字段，灵活支持动态字段处理。
 */
@Slf4j
public class CustomMappingJackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {

    /**
     * 重写父类的 `read` 方法，在读取请求体时添加解密逻辑。
     *
     * @param type         目标对象的类型
     * @param contextClass 上下文类
     * @param inputMessage 包含请求体和头部信息的输入消息
     * @return 转换后的对象
     * @throws IOException 处理过程中可能抛出的异常
     */
    @Override
    public Object read(Type type, Class<?> contextClass, HttpInputMessage inputMessage) throws IOException {
        // 获取当前处理的目标方法
        Method currentMethod = getCurrentMethod();
        if (ObjectUtils.isEmpty(currentMethod)) {
            // 如果没有目标方法，直接调用父类逻辑
            return super.read(type, contextClass, inputMessage);
        }

        // 检查方法是否标记了 @DecryptRequest 注解
        DecryptRequest annotation = currentMethod.getAnnotation(DecryptRequest.class);
        DecryptController decryptController = contextClass.getAnnotation(DecryptController.class);
        CryptController cryptController = contextClass.getAnnotation(CryptController.class);

        if (ObjectUtils.isEmpty(annotation) && ObjectUtils.isEmpty(decryptController)
                && ObjectUtils.isEmpty(cryptController)) {
            // 如果没有注解，直接调用父类逻辑
            return super.read(type, contextClass, inputMessage);
        }

        boolean allParamsDecrypt = false;

        if (annotation != null){
            allParamsDecrypt = annotation.allParamsDecrypt();
        }
        if (decryptController != null){
            allParamsDecrypt = allParamsDecrypt || decryptController.allParamsDecrypt();
        }
        if (decryptController != null){
            allParamsDecrypt = allParamsDecrypt || cryptController.allParamsDecrypt();
        }

        if(allParamsDecrypt){
            return super.read(type, contextClass, inputMessage);
        }

        try {
            // 获取请求体 InputStream
            InputStream inputStream = inputMessage.getBody();

            // 将 InputStream 转换为 String
            String body = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
            body = "{}";

            // 使用修改后的 JSON 重新构建 InputStream
            ByteArrayInputStream updatedInputStream = new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8));

            // 构造新的 HttpInputMessage，以便父类逻辑读取
            HttpInputMessage updatedMessage = new HttpInputMessage() {
                @Override
                public InputStream getBody() {
                    return updatedInputStream;
                }

                @Override
                public HttpHeaders getHeaders() {
                    return inputMessage.getHeaders();
                }
            };

            // 调用父类方法继续处理请求
            return super.read(type, contextClass, updatedMessage);
        } catch (Exception e) {
            // 捕获异常并回退到父类逻辑
            log.error("Error during request decryption", e);
            return super.read(type, contextClass, inputMessage);
        }
    }

    /**
     * 获取当前处理的目标方法。
     *
     * @return 当前目标方法，如果无法获取则返回 null
     */
    public static Method getCurrentMethod() {
        // 获取当前请求的上下文
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        HttpServletRequest request = attributes.getRequest();

        // 从请求属性中获取匹配的处理器
        Object handler = request.getAttribute("org.springframework.web.servlet.HandlerMapping.bestMatchingHandler");
        if (handler instanceof HandlerMethod handlerMethod) {
            // 返回处理器方法
            return handlerMethod.getMethod();
        }
        return null;
    }
}
