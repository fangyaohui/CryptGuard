package com.example.cryptguard.filter;


import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.net.URI;


/**
 * @FileName RequestDecryptFilter
 * @Description 设置过滤器使得网关层对所有指定的请求进行解密
 * @Author yaoHui
 * @date 2024-12-04
 **/
@Slf4j
@Component
public class RequestDecryptFilter implements WebFilter,Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        log.info("RequestDecryptFilter running");

        ServerHttpRequest request = exchange.getRequest();
        URI originalUri = request.getURI();
        String decryptedPath = "/api/crypt/getTest";
        String decryptedAuthority = originalUri.getScheme() + "://" +  originalUri.getAuthority() + "/api/crypt/getTest";

        log.info("Original URI: {}, Decrypted Path: {}", originalUri, decryptedPath);

        // 创建新的请求对象
        ServerHttpRequest mutatedRequest = request.mutate()
                .path(decryptedPath)
                .uri(URI.create(decryptedAuthority)) // 新解密路径
                .build();

        // 修改请求交换对象
        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(mutatedRequest)
                .build();

        return chain.filter(mutatedExchange);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
