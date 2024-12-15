package com.example.cryptguard.filter;

import com.example.cryptguard.config.CryptGuardProperties;
import com.example.cryptguard.utils.AESUtils;
import com.example.cryptguard.utils.PathMatchUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @FileName RequestDecryptFilter
 * @Description 设置过滤器使得网关层对所有指定的请求进行解密
 * @Author yaoHui
 * @date 2024-12-14
 **/
@Slf4j // 使用 Lombok 注解，自动生成日志记录器
public class RequestDecryptFilter implements WebFilter, Ordered {

    private final CryptGuardProperties cryptGuardProperties;

    // 构造函数，接收一个 CryptGuardProperties 实例
    public RequestDecryptFilter(CryptGuardProperties cryptGuardProperties) {
        this.cryptGuardProperties = cryptGuardProperties;
        log.info("RequestDecryptFilter-CryptGuardProperties is : " + this.cryptGuardProperties.toString());
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        log.info("RequestDecryptFilter running");

        // 获取请求信息
        ServerHttpRequest request = exchange.getRequest();
        URI originalUri = request.getURI();

        // 1. 路径解密处理
        if (!ObjectUtils.isEmpty(originalUri.getPath())) {
            // 解析请求路径并解密
            String decryptedPath = AESUtils.decode(originalUri.getPath().substring(1), cryptGuardProperties.getPrivateKey());
            if (!ObjectUtils.isEmpty(decryptedPath) && PathMatchUtils.isPathMatching(decryptedPath, cryptGuardProperties.getDeCryptUrls())) {
                log.info("Original URI Path: {}, Decrypted Path: {}", originalUri.getPath(), decryptedPath);

                // 用解密后的路径替换请求路径
                ServerHttpRequest mutatedRequest = request.mutate().path(decryptedPath).build();
                exchange = exchange.mutate().request(mutatedRequest).build();
            }
        }

        // 2. 根据请求方法选择处理方式
        HttpMethod method = request.getMethod();
        if (method == HttpMethod.GET) {
            return handleGetRequest(exchange, chain); // 处理 GET 请求
        } else if (method == HttpMethod.POST) {
            return handlePostRequest(exchange, chain); // 处理 POST 请求
        }

        // 对于其他类型的请求，直接传递到链的下一个过滤器
        return chain.filter(exchange);
    }

    /**
     * 解密 GET 请求的查询参数
     */
    private Mono<Void> handleGetRequest(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        Map<String, String> queryParams = request.getQueryParams().toSingleValueMap();

        // 对查询参数进行解密
        Map<String, String> decryptedParams = queryParams.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> AESUtils.decode(entry.getValue(), cryptGuardProperties.getPrivateKey())
                ));

        log.info("Decrypted GET Params: {}", decryptedParams);

        // 将解密后的查询参数转为 MultiValueMap
        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        decryptedParams.forEach(multiValueMap::add);

        // 使用解密后的查询参数替换请求的查询参数
        URI newUri = UriComponentsBuilder.fromUri(request.getURI())
                .replaceQueryParams(multiValueMap)
                .build(true)
                .toUri();

        ServerHttpRequest mutatedRequest = request.mutate().uri(newUri).build();
        ServerWebExchange mutatedExchange = exchange.mutate().request(mutatedRequest).build();

        // 继续链式调用
        return chain.filter(mutatedExchange);
    }

    /**
     * 解密 POST 请求的请求体
     */
    private Mono<Void> handlePostRequest(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // 检查请求是否为 JSON 格式的 POST 请求
        if (MediaType.APPLICATION_JSON.isCompatibleWith(request.getHeaders().getContentType())) {
            // 如果请求体是 JSON 格式，收集请求体中的所有数据块
            return request.getBody()
                    .collectList()  // 将请求体中的多个数据块合并为一个 List
                    .flatMap(dataBuffers -> {
                        // 将 DataBuffer 转为字符串
                        String body = dataBuffers.stream()  // 遍历请求体中的所有 DataBuffer
                                .map(buffer -> {
                                    // 创建字节数组，存放当前 DataBuffer 的内容
                                    byte[] bytes = new byte[buffer.readableByteCount()];
                                    // 读取 DataBuffer 中的内容到字节数组
                                    buffer.read(bytes);
                                    // 将字节数组转为字符串，使用 UTF-8 编码
                                    return new String(bytes, StandardCharsets.UTF_8);
                                })
                                // 将所有 DataBuffer 转为单个字符串
                                .collect(Collectors.joining());

                        // 解密请求体中的 encryptParam 参数
                        // 假设请求体中包含一个加密的参数 encryptParam，该参数包含加密的 JSON 数据
                        Map<String, Object> decryptedBody = decryptJsonBody(body);
                        log.info("Decrypted POST Body: {}", decryptedBody);

                        // 构建新的请求体
                        return chain.filter(exchange.mutate()
                                // 用解密后的请求体替换原始请求体
                                .request(new ServerHttpRequestDecorator(request) {
                                    @Override
                                    public Flux<DataBuffer> getBody() {
                                        try {
                                            // 将解密后的 Map 转为 JSON 字符串
                                            byte[] newBodyBytes = new ObjectMapper().writeValueAsBytes(decryptedBody);
                                            // 将 JSON 字符串转换为 DataBuffer
                                            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(newBodyBytes);
                                            // 返回新的 DataBuffer 作为请求体
                                            return Flux.just(buffer); // 返回一个包含新请求体的 Flux
                                        } catch (Exception e) {
                                            // 如果转换过程中出现异常，记录错误并返回错误
                                            log.error("Error while creating new request body", e);
                                            return Flux.error(e); // 返回错误的 Flux
                                        }
                                    }
                                }).build());
                    });
        }

        // 对于非 JSON 类型的 POST 请求，直接传递到下一个过滤器
        return chain.filter(exchange);
    }

    /**
     * 解密 JSON 格式的请求体
     * 假设请求体中包含 encryptParam 字段，字段值是整个加密的请求参数体
     */
    private Map<String, Object> decryptJsonBody(String body) {
        try {
            // 使用 Jackson ObjectMapper 解析 JSON 字符串
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> bodyMap = objectMapper.readValue(body, new TypeReference<Map<String, Object>>() {});

            // 从请求体中提取加密的参数 encryptParam
            String encryptedParam = (String) bodyMap.get("encryptParam");

            // 解密 encryptParam 参数
            String decryptedParam = AESUtils.decode(encryptedParam, cryptGuardProperties.getPrivateKey());

            // 将解密后的参数重新放入 Map
            ObjectMapper paramMapper = new ObjectMapper();
            Map<String, Object> decryptedParamMap = paramMapper.readValue(decryptedParam, new TypeReference<Map<String, Object>>() {});

            log.info("Decrypted encryptParam: {}", decryptedParamMap);

            // 返回解密后的参数体
            return decryptedParamMap;
        } catch (Exception e) {
            log.error("Failed to decrypt JSON body", e);
            return new LinkedHashMap<>(); // 返回空的 Map，避免请求处理失败
        }
    }

    @Override
    public int getOrder() {
        // 过滤器的执行顺序，返回 HIGHEST_PRECEDENCE 表示最优先执行
        return Ordered.HIGHEST_PRECEDENCE;
    }
}

