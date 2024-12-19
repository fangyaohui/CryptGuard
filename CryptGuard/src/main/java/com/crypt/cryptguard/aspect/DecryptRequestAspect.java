package com.crypt.cryptguard.aspect;

import com.crypt.cryptguard.annotation.DecryptRequest;
import com.crypt.cryptguard.utils.AESUtils; // 导入AES解密工具类
import com.fasterxml.jackson.databind.ObjectMapper; // 导入ObjectMapper用于JSON与Java对象之间的转换
import jakarta.servlet.http.HttpServletRequest; // 导入HttpServletRequest类，用于访问HTTP请求
import jakarta.servlet.http.HttpServletRequestWrapper; // 导入HttpServletRequestWrapper，用于包装请求
import lombok.extern.slf4j.Slf4j; // 导入日志记录工具
import org.aspectj.lang.JoinPoint; // 导入JoinPoint，用于获取方法信息
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect; // 导入Aspect注解，表示这是一个切面
import org.aspectj.lang.annotation.Before; // 导入Before注解，表示在方法执行前运行
import org.aspectj.lang.annotation.Pointcut; // 导入Pointcut注解，用于定义切点
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component; // 导入Component注解，将该类标记为Spring组件
import org.springframework.util.ObjectUtils; // 导入ObjectUtils类，用于检查空值
import org.springframework.web.context.request.RequestContextHolder; // 导入RequestContextHolder，用于访问请求上下文
import org.springframework.web.context.request.ServletRequestAttributes; // 导入ServletRequestAttributes，用于获取请求属性
import org.springframework.web.util.ContentCachingRequestWrapper; // 导入ContentCachingRequestWrapper，用于缓存请求内容

import java.lang.reflect.Field; // 导入反射的Field类，用于访问字段
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
public class DecryptRequestAspect {

    // 创建一个ObjectMapper实例，用于JSON和Java对象的转换
    private final static ObjectMapper objectMapper = new ObjectMapper();

    // 设置私钥，用于AES解密
    private final static String privateKey = "fang";

    // 定义一个切点，匹配带有@DecryptRequest注解的方法
    @Pointcut("@annotation(com.crypt.cryptguard.annotation.DecryptRequest)")
    public void decryptRequestPointCut(){
        // 切点方法体为空，表示切点的定义，目标方法会根据此注解被拦截
    }

    @Around("decryptRequestPointCut()")
    public Object handleDecryptRequestPointCut(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        // 获取当前请求的属性
        ServletRequestAttributes servletRequestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        // 如果没有请求属性，则返回
        if(ObjectUtils.isEmpty(servletRequestAttributes)){
            log.info("decryptRequestPointCut ServletRequestAttributes is null");
            return null;
        }

        // 获取HttpServletRequest对象
        HttpServletRequest httpServletRequest = servletRequestAttributes.getRequest();

        // 检查请求是否被包装为ContentCachingRequestWrapper类型
        if(!(httpServletRequest instanceof HttpServletRequestWrapper)){
            log.info("Request is not wrapped in ContentCachingRequestWrapper");
            return null;
        }

        return proceedingJoinPoint.proceed();
    }

    // 定义一个Before通知，表示在目标方法执行前进行解密处理
//    @Before("decryptRequestPointCut()")
    public void handleDecryptRequestPointCutBefore(JoinPoint joinPoint) throws Throwable {

        // 获取当前请求的属性
        ServletRequestAttributes servletRequestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        // 如果没有请求属性，则返回
        if(ObjectUtils.isEmpty(servletRequestAttributes)){
            log.info("decryptRequestPointCut ServletRequestAttributes is null");
            return;
        }

        // 获取HttpServletRequest对象
        HttpServletRequest httpServletRequest = servletRequestAttributes.getRequest();

        // 检查请求是否被包装为ContentCachingRequestWrapper类型
        if(!(httpServletRequest instanceof HttpServletRequestWrapper)){
            log.info("Request is not wrapped in ContentCachingRequestWrapper");
            return;
        }

        // 将请求包装为ContentCachingRequestWrapper，获取原始请求体
        ContentCachingRequestWrapper wrapperRequest = (ContentCachingRequestWrapper) httpServletRequest;
        String originalBody = new String(wrapperRequest.getContentAsByteArray(), wrapperRequest.getCharacterEncoding());

        // 将原始请求体转为Map<String, String>
        Map<String, Object> paramsMap = objectMapper.readValue(originalBody, Map.class);

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        DecryptRequest decryptRequest = methodSignature.getMethod().getAnnotation(DecryptRequest.class);
        String decryptedParams = originalBody;
        if(decryptRequest.allParamsDecrypt()){
            decryptedParams = AESUtils.decode((String) paramsMap.getOrDefault("encryptParam", ""), privateKey);
        }else if(decryptRequest.decryptValuesOnly()){
            for(Map.Entry<String,Object> entry : paramsMap.entrySet()){
                Object param = entry.getValue();
            }
            decryptedParams = paramsMap.toString();
        }else{

        }

        // 解密请求中的"encryptParam"字段，使用AES解密


        // 将解密后的字符串转换为Map<String, Object>类型
        Map<String, Object> decryptedParamsMap = objectMapper.readValue(decryptedParams, Map.class);
        log.info("doDecryptRequestPointCut decryptedObject is {}", decryptedParamsMap);

        // 获取目标方法的参数
        Object[] args = joinPoint.getArgs();
        Object targetObject = args[0]; // 获取第一个参数的实例（假设第一个参数是需要解密的对象）
        Class<?> targetClass = targetObject.getClass();
        Field[] fields = targetClass.getDeclaredFields(); // 获取所有字段（包括私有字段）

        // 遍历字段进行解密赋值
        for (Field field : fields) {
            field.setAccessible(true); // 设置字段可访问

            String fieldName = field.getName(); // 获取字段名称
            Object fieldValue = decryptedParamsMap.getOrDefault(fieldName, null); // 获取解密后的值

            if (fieldValue != null) {
                Class<?> fieldType = field.getType(); // 获取字段类型
                try {
                    // 根据字段类型进行不同的赋值操作
                    if (fieldType == float.class) {
                        // 如果字段是基本类型float
                        field.setFloat(targetObject, Float.parseFloat(fieldValue.toString()));
                    } else if (fieldType == Float.class) {
                        // 如果字段是包装类Float
                        field.set(targetObject, ((Double) fieldValue).floatValue());
                    } else {
                        // 其他类型的字段，使用ObjectMapper转换值为字段类型
                        Object fieldObject = objectMapper.convertValue(fieldValue, fieldType);
                        field.set(targetObject, fieldObject); // 将转换后的值赋给字段
                    }
                } catch (Exception e) {
                    // 如果赋值失败，记录错误日志
                    log.error("Failed to convert value to {} for field: {}, exception is {}", fieldType, fieldName, e);
                }
            }
        }

        // 打印日志表示解密操作完成
        log.info("doDecryptRequestPointCut is running");
    }
}
