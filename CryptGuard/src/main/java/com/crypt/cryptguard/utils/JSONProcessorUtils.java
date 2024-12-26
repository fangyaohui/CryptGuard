package com.crypt.cryptguard.utils;

import com.crypt.cryptguard.annotation.DecryptTransient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @FileName JSONProcessorUtils
 * @Description 处理JSON数据的工具类，支持动态字段转换和加密处理。
 *               提供基于字段类型、注解或特性进行递归节点处理的功能。
 * @Author yaoHui
 * @date 2024-12-22
 **/
@Slf4j
public class JSONProcessorUtils {

    // 单例模式的 ObjectMapper，用于JSON解析和生成
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // 标识已加密字段的后缀
    private static final String cryptString = "_crypt";

    // 定义需要直接处理的基础类型集合
    private static final Set<Class<?>> classSet = new HashSet<>() {{
        add(int.class);
        add(boolean.class);
        add(float.class);
        add(long.class);
        add(Integer.class);
        add(Long.class);
        add(Boolean.class);
        add(Float.class);
        add(String.class);
    }};

    /**
     * 处理输入的JSON字符串，基于指定类的字段特性进行转换。
     *
     * @param json 输入的JSON字符串
     * @param clazz 指定的类类型
     * @return 处理后的JSON字符串
     * @throws JsonProcessingException JSON解析异常
     * @throws IllegalAccessException 反射访问字段异常
     */
    public static String processJson(String json, Class<?> clazz) throws JsonProcessingException, IllegalAccessException {
        // 将JSON字符串解析为JsonNode对象
        JsonNode rootNode = objectMapper.readTree(json);
        // 处理根节点
        processNode(rootNode, clazz, false);
        // 将处理后的JsonNode对象转为字符串返回
        return objectMapper.writeValueAsString(rootNode);
    }

    /**
     * 递归处理JsonNode，根据类字段或指定的解密规则进行节点转换。
     *
     * @param node 当前要处理的JsonNode节点
     * @param clazz 指定的类类型，用于解析字段
     * @param isAllDecrypt 是否强制解密所有字段
     * @throws IllegalAccessException 反射访问字段异常
     */
    private static void processNode(JsonNode node, Class<?> clazz, Boolean isAllDecrypt) throws IllegalAccessException {
        // 如果当前节点不是对象节点，直接返回
        if (!(node instanceof ObjectNode objectNode)) {
            return;
        }

        // 特殊处理：如果类是Object且需要解密，则遍历所有字段
        if (clazz == Object.class && isAllDecrypt) {
            // 存储所有需要修改的字段
            List<Map.Entry<String, JsonNode>> entriesToProcess = new ArrayList<>();
            objectNode.fields().forEachRemaining(entriesToProcess::add);

            // 遍历并处理字段
            for (Map.Entry<String, JsonNode> entry : entriesToProcess) {
                try {
                    String key = entry.getKey();
                    JsonNode valueNode = entry.getValue();

                    if (valueNode.isObject()) {
                        // 如果值是对象，递归处理
                        ObjectNode newValueNode = (ObjectNode) valueNode.deepCopy();
                        processNode(newValueNode, Object.class, true);
                        objectNode.set(key + cryptString, newValueNode); // 设置新字段
                    } else if (valueNode.isArray()) {
                        // 如果值是数组（List类型），处理每个元素
                        ArrayNode newArrayNode = objectNode.putArray(key + cryptString);
                        for (JsonNode item : valueNode) {
                            if (item.isObject()) {
                                ObjectNode newItemNode = (ObjectNode) item.deepCopy();
                                processNode(newItemNode, Object.class, true);
                                newArrayNode.add(newItemNode);
                            } else {
                                newArrayNode.add(item);
                            }
                        }
                    } else {
                        // 基本类型字段的直接处理
                        objectNode.set(key + cryptString, valueNode);
                    }
                    // 删除原字段
                    objectNode.remove(key);
                } catch (Exception e) {
                    // 捕获异常并抛出运行时异常
                    throw new RuntimeException("字段处理出错: " + entry.getKey(), e);
                }
            }
            return ;
        }

        // 遍历类的所有字段，进行处理
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);

            // 判断字段是否需要解密处理
            boolean shouldDecrypt = field.isAnnotationPresent(DecryptTransient.class) || isAllDecrypt;

            // 基本类型字段的处理
            if (classSet.contains(field.getType())) {
                String fieldName = field.getName();
                if (objectNode.has(fieldName) && shouldDecrypt) {
                    JsonNode value = objectNode.remove(fieldName);
                    objectNode.set(fieldName + cryptString, value);
                }
            }
            // 处理List类型字段
            else if (field.getType() == List.class) {
                Type genericType = field.getGenericType();
                if (genericType instanceof ParameterizedType parameterizedType) {
                    Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                    if (actualTypeArguments.length > 0) {
                        Class<?> genericClass = (Class<?>) actualTypeArguments[0];
                        log.info("List的泛型类型为: {}", genericClass.getName());

                        String fieldName = field.getName();
                        if (objectNode.has(fieldName)) {
                            JsonNode listNode = objectNode.get(fieldName);
                            if (listNode.isArray()) {
//                                ArrayNode newArrayNode = objectNode.get(fieldName + cryptString);
                                for (JsonNode itemNode : listNode) {
                                    if (itemNode.isObject()) {
//                                        ObjectNode newItemNode = (ObjectNode) itemNode.deepCopy();
                                        // 根据条件调整 isAllDecrypt 值向下递归
                                        processNode(itemNode, genericClass, shouldDecrypt);
//                                        newArrayNode.add(newItemNode);
                                    }
//                                    else {
//                                        newArrayNode.add(itemNode);
//                                    }
                                }
//                                objectNode.remove(fieldName);
                            } else {
                                log.warn("字段 '{}' 不是JSON数组.", fieldName);
                            }
                        }
                    }
                } else {
                    log.warn("字段缺少泛型类型信息.");
                }
            }
            // 处理Map类型字段
            else if (Map.class.isAssignableFrom(field.getType())) {
                String fieldName = field.getName();
                if (objectNode.has(fieldName)) {
                    JsonNode mapNode = objectNode.get(fieldName);
                    if (mapNode.isObject()) {
                        ObjectNode newMapNode = objectNode.putObject(fieldName);
                        mapNode.fields().forEachRemaining(entry -> {
                            try {
                                String key = entry.getKey();
                                JsonNode valueNode = entry.getValue();

                                if (valueNode.isObject()) {
                                    // 递归处理Map的值为对象的情况
                                    ObjectNode newValueNode = (ObjectNode) valueNode.deepCopy();
                                    // 根据条件调整 isAllDecrypt 值向下递归
                                    processNode(newValueNode, Object.class, shouldDecrypt);
                                    newMapNode.set(key, newValueNode);
                                } else {
                                    if (shouldDecrypt){
                                        newMapNode.set(key + cryptString, valueNode); // 添加后缀
                                    }else{
                                        newMapNode.set(key, valueNode); // 不添加添加后缀
                                    }
                                }
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    } else {
                        log.warn("字段 '{}' 不是JSON对象（Map类型）.", fieldName);
                    }
                }
            }else{
                String fieldName = field.getName();
                if(objectNode.has(fieldName)){
                    JsonNode mapNode = objectNode.get(fieldName);
                    processNode(mapNode, Object.class, shouldDecrypt);
                }

            }

        }

    }
}
