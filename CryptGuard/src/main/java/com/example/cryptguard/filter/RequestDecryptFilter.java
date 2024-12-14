package com.example.cryptguard.filter;


import com.example.cryptguard.config.CryptGuardProperties;
import com.example.cryptguard.utils.PathMatchUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
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
public class RequestDecryptFilter implements WebFilter,Ordered {

    private final CryptGuardProperties cryptGuardProperties;

    public RequestDecryptFilter(CryptGuardProperties cryptGuardProperties){
        this.cryptGuardProperties = cryptGuardProperties;
        log.info("RequestDecryptFilter-CryptGuardProperties is : " + this.cryptGuardProperties.toString());
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        log.info("RequestDecryptFilter running");

        ServerHttpRequest request = exchange.getRequest();
        URI originalUri = request.getURI();
        String decryptedPath = originalUri.getPath();

        // 判断解密之后的URL是否在用户配置的加密URL里，如果不在则不需要解密操作
        if(!PathMatchUtils.isPathMatching(decryptedPath,cryptGuardProperties.getDeCryptUrls())){
            log.info("RequestDecryptFilter 该URL无需解密操作：" + originalUri);
            return chain.filter(exchange);
        }

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
