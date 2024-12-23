package com.crypt.cryptguard.aspect;

import com.crypt.cryptguard.annotation.CryptTransient;
import com.crypt.cryptguard.annotation.DecryptRequest;
import com.crypt.cryptguard.annotation.DecryptTransient;
import com.crypt.cryptguard.utils.AESUtils; // 导入AES解密工具类
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper; // 导入ObjectMapper用于JSON与Java对象之间的转换
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
import java.util.ArrayList;
import java.util.HashMap;
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

    // 定义一个Before通知，表示在目标方法执行前进行解密处理
    @Before("decryptRequestPointCut()")
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
        // 获取目标方法的参数
        Object[] args = joinPoint.getArgs();
        Object targetObject = args[0]; // 获取第一个参数的实例（假设第一个参数是需要解密的对象）
        Class<?> targetClass = targetObject.getClass();
        Field[] fields = targetClass.getDeclaredFields(); // 获取所有字段（包括私有字段）

        if(decryptRequest.partialParamsDecrypt()){
            // 部分属性进行解密&注入
            paramsMap = decryptOriginalParams(paramsMap,targetClass,false);
            invokeFieldValue(fields,targetObject,paramsMap,false);
        }else if(decryptRequest.decryptValuesOnly()){
            // 所有属性进行解密&注入
            paramsMap = decryptOriginalParams(paramsMap,targetClass,true);
            invokeFieldValue(fields,targetObject,paramsMap,true);
        }else{

        }

        // 打印日志表示解密操作完成
        log.info("doDecryptRequestPointCut is running");
    }

    // 根据传入的paramsMap和TargetObject把属性注入进去
    public void invokeFieldValue(Field[] fields,Object targetObject,Map<String, Object> paramsMap,Boolean isAllFieldInvoke){

        for(Field field : fields){
            if(!isAllFieldInvoke && !field.isAnnotationPresent(DecryptTransient.class)){
                continue;
            }

            field.setAccessible(true); // 设置字段可访问

            String fieldName = field.getName(); // 获取字段名称
            Object fieldValue = paramsMap.getOrDefault(fieldName, null); // 获取解密后的值

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
    }

    public Map<String,Object> decryptOriginalParams(Map<String,Object> paramsMap,Class<?> targetClass,Boolean isAllParamsDecrypt){

        // 全部都需要进行解密

        return paramsMap;
    }

    /**
     * 对 JSON 数据进行递归解密
     *
     * @param node 当前需要解密的 JSON 节点
     * @return 解密后的 JSON 节点
     */
    public static JsonNode decryptJson(JsonNode node) {
        if (node.isObject()) {
            // 如果是 ObjectNode，则递归解密其每个字段
            ObjectNode objectNode = (ObjectNode) node;
            objectNode.fieldNames().forEachRemaining(fieldName -> {
                JsonNode fieldValue = objectNode.get(fieldName);
                // 递归处理字段值
                objectNode.set(fieldName, decryptJson(fieldValue));
            });
            return objectNode;
        } else if (node.isArray()) {
            // 如果是 ArrayNode，则递归处理每个元素
            ArrayNode arrayNode = (ArrayNode) node;
            for (int i = 0; i < arrayNode.size(); i++) {
                JsonNode element = arrayNode.get(i);
                // 替换为解密后的元素
                arrayNode.set(i, decryptJson(element));
            }
            return arrayNode;
        } else if (node.isTextual()) {
            // 如果是文本节点，执行解密
            String decryptedValue = AESUtils.decode(node.asText(), privateKey);
            return objectMapper.getNodeFactory().textNode(decryptedValue);
        }
        // 对于其他类型（数字、布尔等）直接返回
        return node;
    }
}
