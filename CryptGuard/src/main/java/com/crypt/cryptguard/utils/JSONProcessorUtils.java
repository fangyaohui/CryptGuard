package com.crypt.cryptguard.utils;

import com.crypt.cryptguard.annotation.CryptTransient;
import com.crypt.cryptguard.annotation.DecryptTransient;
import com.crypt.cryptguard.annotation.EncryptTransient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @FileName JSONProcessorUtils
 * @Description JSON处理工具类，支持字段加密和解密的动态转换。
 * @Author yaoHui
 * @date 2024-12-22
 **/
@Slf4j
public class JSONProcessorUtils {

    // 单例 ObjectMapper 实例，用于 JSON 解析和生成
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    // 加密字段的后缀标识
    private static final String CRYPT_SUFFIX = "_crypt";

    // AES 加密的私钥
    private static final String PRIVATE_KEY = "fang";

    // 定义直接处理的基础类型集合
    private static final Set<Class<?>> BASE_TYPES = Set.of(
            int.class, boolean.class, float.class, long.class,
            Integer.class, Long.class, Boolean.class, Float.class, String.class
    );

    /**
     * 处理输入的 JSON 字符串，根据类字段特性进行加密或解密。
     *
     * @param json      输入的 JSON 字符串
     * @param clazz     指定的类类型，用于确定字段特性
     * @param isEncrypt 是否加密处理，false 则为解密处理
     * @return 处理后的 JSON 字符串
     * @throws JsonProcessingException JSON 解析异常
     * @throws IllegalAccessException  反射访问字段异常
     */
    public static String processJson(String json, Class<?> clazz, boolean isEncrypt)
            throws JsonProcessingException, IllegalAccessException {
        // 解析 JSON 字符串为 JsonNode
        JsonNode rootNode = OBJECT_MAPPER.readTree(json);
        // 递归处理节点
        processCryptNode(rootNode, clazz, false, isEncrypt);
        // 将处理后的 JsonNode 转为 JSON 字符串返回
        return OBJECT_MAPPER.writeValueAsString(rootNode);
    }

    /**
     * 递归处理 JsonNode，根据类字段特性进行加密或解密。
     *
     * @param node         当前处理的 JsonNode 节点
     * @param clazz        指定的类类型
     * @param isAllProcess 是否强制处理所有字段
     * @param isEncrypt    是否加密处理，false 则为解密处理
     * @throws IllegalAccessException 反射访问字段异常
     */
    private static void processCryptNode(JsonNode node, Class<?> clazz,
                                         boolean isAllProcess, boolean isEncrypt) throws IllegalAccessException {
        // 如果当前节点不是对象类型，直接返回
        if (!(node instanceof ObjectNode objectNode)) {
            return;
        }

        // 如果类是 Object 且需要处理所有字段，直接遍历所有键值对
        if (clazz == Object.class) {
            processObjectNode(objectNode, isEncrypt);
            return;
        }

        if(isEncrypt){
            isAllProcess = clazz.isAnnotationPresent(EncryptTransient.class) ||
                    clazz.isAnnotationPresent(CryptTransient.class) || isAllProcess;
        }else{
            isAllProcess = clazz.isAnnotationPresent(DecryptTransient.class) ||
                    clazz.isAnnotationPresent(CryptTransient.class) || isAllProcess;
        }

        // 遍历类的所有字段，依据字段特性逐一处理
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);

            String fieldName = field.getName(); // 字段名称
            boolean shouldProcess = shouldFieldBeProcessed(field, isAllProcess, isEncrypt); // 判断是否需要处理

            // 根据字段类型选择处理逻辑
            if (BASE_TYPES.contains(field.getType())) {
                processBasicTypeField(objectNode, fieldName, shouldProcess, isEncrypt);
            } else if (field.getType() == List.class) {
                processListField(objectNode, field, fieldName, shouldProcess, isEncrypt);
            } else if (Map.class.isAssignableFrom(field.getType())) {
                processMapField(objectNode, fieldName, shouldProcess, isEncrypt);
            } else {
                // 非基础类型字段递归处理
                processCryptNode(objectNode.get(fieldName), field.getType(), shouldProcess, isEncrypt);
            }
        }
    }

    /**
     * 处理 ObjectNode。
     * 支持递归处理嵌套的对象和数组，直接在传入的 ObjectNode 上操作。
     *
     * @param objectNode 当前对象节点
     * @param isEncrypt  是否加密
     */
    private static void processObjectNode(ObjectNode objectNode, boolean isEncrypt) {
        objectNode.fields().forEachRemaining(entry -> {
            String key = entry.getKey();
            JsonNode valueNode = entry.getValue();

            if (valueNode.isTextual()) {
                // 如果值是文本类型，处理加密或解密
                objectNode.put(key, processValue(valueNode.asText(), isEncrypt));
            } else if (valueNode.isObject()) {
                // 如果值是嵌套的 ObjectNode，递归处理
                processObjectNode((ObjectNode) valueNode, isEncrypt);
            } else if (valueNode.isArray()) {
                // 如果值是嵌套的 ArrayNode，递归处理
                ArrayNode processedArray = processArrayNode((ArrayNode) valueNode, isEncrypt);
                objectNode.set(key, processedArray);
            }
            // 其他类型无需处理，保持原样
        });
    }

    /**
     * 判断字段是否需要处理。
     *
     * @param field       字段
     * @param isAllProcess 是否强制处理所有字段
     * @param isEncrypt   是否加密
     * @return 是否需要处理
     */
    private static boolean shouldFieldBeProcessed(Field field, boolean isAllProcess, boolean isEncrypt) {
        return isAllProcess || (isEncrypt && field.isAnnotationPresent(EncryptTransient.class))
                || (!isEncrypt && field.isAnnotationPresent(DecryptTransient.class));
    }

    /**
     * 处理基础类型字段。
     */
    private static void processBasicTypeField(ObjectNode objectNode, String fieldName, boolean shouldProcess, boolean isEncrypt) {
        if (shouldProcess && objectNode.has(fieldName)) {
            JsonNode valueNode = objectNode.get(fieldName);
            objectNode.set(fieldName, new TextNode(processValue(valueNode.asText(), isEncrypt)));
        }
    }

    /**
     * 处理 List 类型字段。
     */
    private static void processListField(ObjectNode objectNode, Field field, String fieldName,
                                         boolean shouldProcess, boolean isEncrypt) throws IllegalAccessException {
        if (!objectNode.has(fieldName)) {
            return;
        }
        JsonNode listNode = objectNode.get(fieldName);
        if (listNode.isArray()) {
            ArrayNode newArrayNode = objectNode.putArray(fieldName);
            Type genericType = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
            Class<?> genericClass = (Class<?>) genericType;

            for (JsonNode itemNode : listNode) {
                if (itemNode.isObject()) {
                    processCryptNode(itemNode, genericClass, shouldProcess, isEncrypt);
                    newArrayNode.add(itemNode);
                } else if (shouldProcess) {
                    newArrayNode.add(processValue(itemNode.asText(), isEncrypt));
                } else {
                    newArrayNode.add(itemNode);
                }
            }
        }
    }

    /**
     * 处理 Map 类型字段。
     */
    private static void processMapField(ObjectNode objectNode, String fieldName, boolean shouldProcess, boolean isEncrypt) {
        if (objectNode.has(fieldName)) {
            JsonNode mapNode = objectNode.get(fieldName);

            if (mapNode.isObject()) {
                // 处理 Map 类型字段
                ObjectNode objectMapNode = (ObjectNode) mapNode;
                objectMapNode.fields().forEachRemaining(entry -> {
                    String key = entry.getKey();
                    JsonNode valueNode = entry.getValue();

                    if (valueNode.isTextual() && shouldProcess) {
                        objectMapNode.put(key, processValue(valueNode.asText(), isEncrypt));
                    } else if (valueNode.isObject()) {
                        // 如果是嵌套的 Map 类型，递归处理
                        processMapField(objectMapNode, key, shouldProcess, isEncrypt);
                    } else if (valueNode.isArray()) {
                        // 如果是 List 类型，递归处理每一个元素
                        processArrayNode((ArrayNode) valueNode, shouldProcess, isEncrypt);
                    }
                });
            }
        }
    }

    private static void processArrayNode(ArrayNode arrayNode, boolean shouldProcess, boolean isEncrypt) {
        for (int i = 0; i < arrayNode.size(); i++) {
            JsonNode element = arrayNode.get(i);

            if (element.isTextual() && shouldProcess) {
                arrayNode.set(i, new TextNode(processValue(element.asText(), isEncrypt)));
            } else if (element.isObject()) {
                // 如果元素是 Map 类型，递归处理
                processMapField((ObjectNode) element, "nestedMapField", shouldProcess, isEncrypt);
            }
        }
    }

    /**
     * 处理字符串值，加密或解密。
     */
    private static String processValue(String value, boolean isEncrypt) {
        return isEncrypt ? AESUtils.encode(value, PRIVATE_KEY) : AESUtils.decode(value, PRIVATE_KEY);
    }

    /**
     * 处理 ArrayNode。
     */
    private static ArrayNode processArrayNode(ArrayNode arrayNode, boolean isEncrypt) {
        ArrayNode newArrayNode = OBJECT_MAPPER.createArrayNode();
        arrayNode.forEach(item -> {
            if (item.isTextual()) {
                // 处理文本类型的值
                newArrayNode.add(processValue(item.asText(), isEncrypt));
            } else if (item.isObject()) {
                // 递归处理 ObjectNode
                processObjectNode((ObjectNode) item, isEncrypt);
                newArrayNode.add(item);
            } else if (item.isArray()) {
                // 递归处理嵌套的 ArrayNode
                newArrayNode.add(processArrayNode((ArrayNode) item, isEncrypt));
            } else {
                // 直接添加其他类型的值
                newArrayNode.add(item);
            }
        });
        return newArrayNode;
    }

}
