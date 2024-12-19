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
        Map<String,Object> decryptedParamsMap = objectMapper.readValue(decryptedParams, Map.class);
        log.info("doDecryptRequestPointCut decryptedObject is {}",decryptedParamsMap);

        Object[] args = joinPoint.getArgs();
        Object targetObject = args[0]; // 获取第一个参数实例
        Class<?> targetClass = targetObject.getClass();
        Field[] fields = targetClass.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            String fieldName = field.getName();
            Object fieldValue =  decryptedParamsMap.getOrDefault(fieldName, null);

            if (fieldValue != null) {
                Class<?> fieldType = field.getType();
                try{
                    if(fieldType == float.class){
                        field.setFloat(targetObject,Float.parseFloat(fieldValue.toString()));
                    }else if (fieldType == Float.class){
                        field.set(targetObject,((Double)fieldValue).floatValue());
                    }else{
                        Object fieldObject = objectMapper.convertValue(fieldValue,fieldType);
                        field.set(targetObject,fieldObject);
                    }
                }catch (Exception e){
                    log.error("Failed to convert value to {} for field: {},exception is {}",fieldType, fieldName,e);
                }
            }
        }

        log.info("doDecryptRequestPointCut is running");
    }
}
