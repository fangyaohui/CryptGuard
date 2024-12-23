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
 * @Description
 * @Author yaoHui
 * @date 2024-12-22
 **/
@Slf4j
public class JSONProcessorUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final String cryptString = "_crypt";

    private static final Set<Class<?>> classSet = new HashSet<>(){{
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


    public static String processJson(String json, Class<?> clazz) throws JsonProcessingException, IllegalAccessException {
        JsonNode rootNode = objectMapper.readTree(json);
        processNode(rootNode, clazz, false);
        return objectMapper.writeValueAsString(rootNode);
    }

    private static void processNode(JsonNode node, Class<?> clazz, Boolean isAllDecrypt) throws IllegalAccessException {
        if (!(node instanceof ObjectNode objectNode)) {
            return;
        }

        // 如果 clazz 是 Object.class 且需要解密，遍历所有字段
        if (clazz == Object.class && isAllDecrypt) {
            // 存储所有需要修改的字段
            List<Map.Entry<String, JsonNode>> entriesToProcess = new ArrayList<>();

            // 遍历所有字段并收集需要修改的字段
            objectNode.fields().forEachRemaining(entriesToProcess::add);

            // 处理字段
            for (Map.Entry<String, JsonNode> entry : entriesToProcess) {
                try {
                    String key = entry.getKey();
                    JsonNode valueNode = entry.getValue();

                    if (valueNode.isObject()) {
                        // 如果值是对象，递归处理（包括 Map 类型的处理）
                        ObjectNode newValueNode = (ObjectNode) valueNode.deepCopy();
                        processNode(newValueNode, Object.class, true);  // 递归处理
                        objectNode.set(key + cryptString, newValueNode);  // 设置新的字段
                    } else if (valueNode.isArray()) {
                        // 如果是数组（List），则需要单独处理每个元素
                        ArrayNode newArrayNode = objectNode.putArray(key + cryptString);
                        for (JsonNode item : valueNode) {
                            if (item.isObject()) {
                                ObjectNode newItemNode = (ObjectNode) item.deepCopy();
                                processNode(newItemNode, Object.class, true);  // 递归处理每个对象
                                newArrayNode.add(newItemNode);
                            } else {
                                newArrayNode.add(item);  // 直接添加非对象元素
                            }
                        }
                    } else {
                        // 对于基本类型（如字符串、数字等），直接处理并重命名字段
                        objectNode.set(key + cryptString, valueNode);
                    }
                    // 删除原有的字段
                    objectNode.remove(key);
                } catch (Exception e) {
                    // 捕获异常并抛出运行时异常
                    throw new RuntimeException("Error processing field: " + entry.getKey(), e);
                }
            }
        }



        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);

            // 判断是否需要解密处理
            if (field.isAnnotationPresent(DecryptTransient.class) || isAllDecrypt) {
                // 基本类型直接替换
                if (classSet.contains(field.getType())) {
                    String fieldName = field.getName();
                    if (objectNode.has(fieldName)) {
                        JsonNode value = objectNode.remove(fieldName);
                        objectNode.set(fieldName + cryptString, value);
                    }
                }
                // 判断是否为 List 类型
                else if (field.getType() == List.class) {
                    Type genericType = field.getGenericType();
                    if (genericType instanceof ParameterizedType parameterizedType) {
                        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                        if (actualTypeArguments.length > 0) {
                            Class<?> genericClass = (Class<?>) actualTypeArguments[0];
                            log.info("The generic type of the List is: {}", genericClass.getName());

                            String fieldName = field.getName();
                            if (objectNode.has(fieldName)) {
                                JsonNode listNode = objectNode.get(fieldName);
                                if (listNode.isArray()) {
                                    ArrayNode newArrayNode = objectNode.putArray(fieldName + cryptString);
                                    for (JsonNode itemNode : listNode) {
                                        if (itemNode.isObject()) {
                                            ObjectNode newItemNode = (ObjectNode) itemNode.deepCopy();
                                            processNode(newItemNode, genericClass, isAllDecrypt);
                                            newArrayNode.add(newItemNode);
                                        } else {
                                            newArrayNode.add(itemNode);
                                        }
                                    }
                                    objectNode.remove(fieldName);
                                } else {
                                    log.warn("The field '{}' is not a JSON array.", fieldName);
                                }
                            }
                        }
                    } else {
                        log.warn("The field does not have parameterized type information.");
                    }
                }
                // 判断是否为 Map 类型
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
                                        // 处理 Map 的值为对象的情况
                                        ObjectNode newValueNode = (ObjectNode) valueNode.deepCopy();
                                        processNode(newValueNode, Object.class, true);
                                        newMapNode.set(key, newValueNode);
                                    } else {
                                        // 直接复制非对象节点
                                        newMapNode.set(key+ cryptString, valueNode);
                                    }
                                } catch (IllegalAccessException e) {
                                    throw new RuntimeException(e);
                                }
                            });
//                            objectNode.remove(fieldName);
                        } else {
                            log.warn("The field '{}' is not a JSON object (Map type).", fieldName);
                        }
                    }
                }
            }
        }
    }
}
