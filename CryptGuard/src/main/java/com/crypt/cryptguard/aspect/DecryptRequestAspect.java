package com.crypt.cryptguard.aspect;

import com.crypt.cryptguard.annotation.CryptController;
import com.crypt.cryptguard.annotation.CryptMethod;
import com.crypt.cryptguard.annotation.DecryptController;
import com.crypt.cryptguard.annotation.DecryptRequest;
import com.crypt.cryptguard.strategy.CryptStrategy;
import com.crypt.cryptguard.strategy.CryptStrategyFactory;
import com.crypt.cryptguard.utils.JSONProcessorUtils;
import com.fasterxml.jackson.databind.ObjectMapper; // 导入ObjectMapper用于JSON与Java对象之间的转换
import jakarta.servlet.http.HttpServletRequest; // 导入HttpServletRequest类，用于访问HTTP请求
import jakarta.servlet.http.HttpServletRequestWrapper; // 导入HttpServletRequestWrapper，用于包装请求
import lombok.extern.slf4j.Slf4j; // 导入日志记录工具
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect; // 导入Aspect注解，表示这是一个切面
import org.aspectj.lang.annotation.Pointcut; // 导入Pointcut注解，用于定义切点
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component; // 导入Component注解，将该类标记为Spring组件
import org.springframework.util.ObjectUtils; // 导入ObjectUtils类，用于检查空值
import org.springframework.web.context.request.RequestContextHolder; // 导入RequestContextHolder，用于访问请求上下文
import org.springframework.web.context.request.ServletRequestAttributes; // 导入ServletRequestAttributes，用于获取请求属性
import org.springframework.web.util.ContentCachingRequestWrapper; // 导入ContentCachingRequestWrapper，用于缓存请求内容

import java.util.Map; // 导入Map，用于存储解密后的请求参数

/**
 * @FileName DecryptRequestAspect
 * @Description 实现 @DecryptRequest 的逻辑，拦截特定方法的请求，对指定范围（URL 和/或参数）进行解密。
 * @Author yaoHui
 * @date 2024-12-15
 **/
@Slf4j // 使用Slf4j日志记录
@Aspect // 表示这是一个切面
@Component // 表示该类为Spring组件
@Order(Integer.MIN_VALUE)
public class DecryptRequestAspect {

    // 创建一个ObjectMapper实例，用于JSON和Java对象的转换
    private final static ObjectMapper objectMapper = new ObjectMapper();

    // 设置私钥，用于AES解密
    private final static String privateKey = "fang";

    private final static CryptStrategy cryptStrategy = CryptStrategyFactory.getStrategy();

    // 定义一个切点，匹配带有@DecryptRequest注解的方法
    @Pointcut("@annotation(com.crypt.cryptguard.annotation.DecryptRequest) " +
            "|| @annotation(com.crypt.cryptguard.annotation.CryptMethod)" +
            "|| @within(com.crypt.cryptguard.annotation.DecryptController)" +
            "|| @within(com.crypt.cryptguard.annotation.CryptController)")
    public void decryptRequestPointCut(){
        // 切点方法体为空，表示切点的定义，目标方法会根据此注解被拦截
    }

    // 定义一个Before通知，表示在目标方法执行前进行解密处理
    @Around("decryptRequestPointCut()")
    public Object handleDecryptRequestPointCutBefore(ProceedingJoinPoint joinPoint) throws Throwable {

        // 获取当前请求的属性
        ServletRequestAttributes servletRequestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        // 如果没有请求属性，则返回
        if(ObjectUtils.isEmpty(servletRequestAttributes)){
            log.info("decryptRequestPointCut ServletRequestAttributes is null");
            return joinPoint.proceed();
        }

        // 获取HttpServletRequest对象
        HttpServletRequest httpServletRequest = servletRequestAttributes.getRequest();

        // 检查请求是否被包装为ContentCachingRequestWrapper类型
        if(!(httpServletRequest instanceof HttpServletRequestWrapper)){
            log.info("Request is not wrapped in ContentCachingRequestWrapper");
            return joinPoint.proceed();
        }

        // 将请求包装为ContentCachingRequestWrapper，获取原始请求体
        ContentCachingRequestWrapper wrapperRequest = (ContentCachingRequestWrapper) httpServletRequest;
        String originalBody = new String(wrapperRequest.getContentAsByteArray(), wrapperRequest.getCharacterEncoding());

        // 将原始请求体转为Map<String, String>
        Map<String, Object> paramsMap = objectMapper.readValue(originalBody, Map.class);

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        DecryptRequest decryptRequest = methodSignature.getMethod().getAnnotation(DecryptRequest.class);
        CryptMethod cryptMethod = methodSignature.getMethod().getAnnotation(CryptMethod.class);
        DecryptController decryptController = joinPoint.getTarget().getClass().getAnnotation(DecryptController.class);
        CryptController cryptController = joinPoint.getTarget().getClass().getAnnotation(CryptController.class);

        boolean allParamsDecrypt = false;

        // 防止 NullPointerException
        if (cryptMethod != null) {
            allParamsDecrypt = cryptMethod.allParamsDecrypt();
        }
        if (decryptRequest != null) {
            allParamsDecrypt = allParamsDecrypt || decryptRequest.allParamsDecrypt();
        }
        if (decryptController != null){
            allParamsDecrypt = allParamsDecrypt || decryptController.allParamsDecrypt();
        }
        if (cryptController != null){
            allParamsDecrypt = allParamsDecrypt || cryptController.allParamsDecrypt();
        }
        String decryptedParams = originalBody;
        // 获取目标方法的参数
        Object[] args = joinPoint.getArgs();
        Object targetObject = args[0]; // 获取第一个参数的实例（假设第一个参数是需要解密的对象）
        Class<?> targetClass = targetObject.getClass();

        if (allParamsDecrypt){
            decryptedParams =  cryptStrategy.decrypt((String) paramsMap.getOrDefault("encryptParam", ""));
            targetObject = objectMapper.readValue(decryptedParams, targetClass);
            log.info("解密处理之后得到的对象为： {}",targetObject.toString());
            args[0] = targetObject;
            // 调用目标方法并返回结果
            return joinPoint.proceed(args);
        }else{
            decryptedParams = JSONProcessorUtils.processJson(decryptedParams, targetClass, false);
            targetObject = objectMapper.readValue(decryptedParams, targetClass);
            log.info("解密处理之后得到的对象为： {}",targetObject.toString());
            args[0] = targetObject;
            // 调用目标方法并返回结果
            return joinPoint.proceed(args);
        }
    }
}
