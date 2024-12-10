package com.example.cryptguard.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * @FileName ResponseEncryptFilter
 * @Description 设置过滤器使得网关层对所有指定的响应进行加密
 * @Author yaoHui
 * @date 2024-12-04
 **/
@Slf4j
@Component
public class ResponseEncryptFilter implements WebFilter, Ordered {

    @Override
    public int getOrder() {
        // 设置拦截器优先级
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        log.info("ResponseEncryptFilter running");

        // 获取响应对象
        ServerHttpResponse response = exchange.getResponse();

//        // 包装响应对象
//        ServerHttpResponseDecorator responseDecorator = new ServerHttpResponseDecorator(originalResponse) {
//            @Override
//            public Mono<Void> writeWith(Flux<? extends DataBuffer> body) {
//                // 拦截响应体流
//                Flux<DataBuffer> modifiedBody = body.map(dataBuffer -> {
//                    // 读取原始响应内容
//                    byte[] content = new byte[dataBuffer.readableByteCount()];
//                    dataBuffer.read(content);
//                    DataBufferUtils.release(dataBuffer);
//
//                    String originalBody = new String(content, StandardCharsets.UTF_8);
//                    log.info("Original Response Body: {}", originalBody);
//
//                    // 如果需要，可以对响应内容进行修改（这里直接返回原内容）
//                    return bufferFactory().wrap(content);
//                });
//
//                // 返回修改后的响应体
//                return super.writeWith(modifiedBody);
//            }
//        };

        // 包装响应数据，进行处理
        return chain.filter(exchange).then(
                Mono.fromRunnable(() -> {
                    // 这里可以对响应进行修改或记录日志
                    log.info("Response status code: {}", response.getStatusCode());
                    response.getHeaders().add("Custom-Header", "ProcessedByEncryptFilter");
                })
        );
    }
}
