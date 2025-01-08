package com.crypt.cryptguard.aspect;

import com.crypt.cryptguard.annotation.CryptMethod;
import com.crypt.cryptguard.annotation.EncryptResponse;
import com.crypt.cryptguard.utils.JSONProcessorUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Method;

/**
 * @FileName EncryptResponseAspect
 * @Description 实现 @EncryptResponse 的逻辑，拦截特定方法的响应数据，对响应内容进行加密。
 * @Author yaoHui
 * @date 2024-12-15
 **/
@Slf4j
@Aspect
@Component
@Order(Integer.MAX_VALUE)
public class EncryptResponseAspect {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Pointcut("@annotation(com.crypt.cryptguard.annotation.EncryptResponse) " +
            "|| @annotation(com.crypt.cryptguard.annotation.CryptMethod)")
    public void encryptResponsePointCut(){

    }


    @Around("encryptResponsePointCut()")
    public Object handleEncryptResponsePointCut(ProceedingJoinPoint joinPoint) throws Throwable {

        // 执行目标方法
        Object result = joinPoint.proceed();

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        EncryptResponse encryptResponse = method.getAnnotation(EncryptResponse.class);
        CryptMethod cryptMethod = method.getAnnotation(CryptMethod.class);

        if (ObjectUtils.isEmpty(encryptResponse) && ObjectUtils.isEmpty(cryptMethod)) {
            return result;
        }

        // 获取原始结果的类型
        Class<?> clazz = result.getClass();

        // 序列化结果为 JSON 字符串
        String resultJson = objectMapper.writeValueAsString(result);
        log.info("Original result JSON: {}", resultJson);

        // 处理 JSON (加密)
        String encryptedJson = JSONProcessorUtils.processJson(resultJson, clazz, true);
        log.info("Encrypted result JSON: {}", encryptedJson);

        // 将加密后的 JSON 反序列化为新的对象

        return objectMapper.readValue(encryptedJson, clazz);
    }


}
