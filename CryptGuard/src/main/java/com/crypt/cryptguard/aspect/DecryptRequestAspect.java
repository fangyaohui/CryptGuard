package com.crypt.cryptguard.aspect;

import com.crypt.cryptguard.utils.AESUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * @FileName DecryptRequestAspect
 * @Description 实现 @DecryptRequest 的逻辑，拦截特定方法的请求，对指定范围（URL 和/或参数）进行解密。
 * @Author yaoHui
 * @date 2024-12-15
 **/
@Slf4j
@Aspect
@Component
public class DecryptRequestAspect {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    private final static String privateKey = "fang";

    @Pointcut("@annotation(com.crypt.cryptguard.annotation.DecryptRequest)")
    public void decryptRequestPointCut(){

    }

    @Before("decryptRequestPointCut()")
    public void handleDecryptRequestPointCut(JoinPoint joinPoint) throws Throwable {

        ServletRequestAttributes servletRequestAttributes =
                (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();

        if(ObjectUtils.isEmpty(servletRequestAttributes)){
            log.info("decryptRequestPointCut ServletRequestAttributes is null");
            return ;
        }

        HttpServletRequest httpServletRequest = servletRequestAttributes.getRequest();

        if(!(httpServletRequest instanceof HttpServletRequestWrapper)){
            log.info("Request is not wrapped in ContentCachingRequestWrapper");
            return ;
        }

        ContentCachingRequestWrapper wrapperRequest = (ContentCachingRequestWrapper) httpServletRequest;
        String originalBody = new String(wrapperRequest.getContentAsByteArray(), wrapperRequest.getCharacterEncoding());
        Map<String,String> paramsMap = objectMapper.readValue(originalBody,Map.class);
        String decryptedParams = AESUtils.decode(paramsMap.getOrDefault("encryptParam",""),privateKey);
        Map<String,String> decryptedParamsMap = objectMapper.readValue(decryptedParams,Map.class);
        log.info("doDecryptRequestPointCut decryptedObject is {}",decryptedParamsMap);

        Object[] args = joinPoint.getArgs();
        Class<?> targetObject = args[0].getClass();  // 获取第一个参数的实际类型
        Field[] fields = targetObject.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            String fieldName = field.getName();
            String fieldValue = (String) decryptedParamsMap.getOrDefault(fieldName, null);

            if (fieldValue != null) {
                Class<?> fieldType = field.getType();

                // 检查是否是String类型，如果是Class类型则进行转换
                if (fieldType == String.class) {
                    // 如果fieldValue是Class类型，避免直接注入
                    field.set(targetObject, new String(fieldValue));
                } else if (fieldType == Integer.class || fieldType == int.class) {
                    try {
                        field.set(targetObject, Integer.valueOf(fieldValue));
                    } catch (NumberFormatException e) {
                        log.error("Failed to convert value to Integer for field: {}", fieldName);
                    }
                } else if (fieldType == Double.class || fieldType == double.class) {
                    try {
                        field.set(targetObject, Double.valueOf(fieldValue));
                    } catch (NumberFormatException e) {
                        log.error("Failed to convert value to Double for field: {}", fieldName);
                    }
                } else if (fieldType == Boolean.class || fieldType == boolean.class) {
                    field.set(targetObject, Boolean.valueOf(fieldValue));
                } else if (fieldType == Long.class || fieldType == long.class) {
                    try {
                        field.set(targetObject, Long.valueOf(fieldValue));
                    } catch (NumberFormatException e) {
                        log.error("Failed to convert value to Long for field: {}", fieldName);
                    }
                }
            }
        }


        log.info("doDecryptRequestPointCut is running");
    }
}
