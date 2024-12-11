package com.example.cryptguard.filter;

import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.ORIGINAL_RESPONSE_CONTENT_TYPE_ATTR;

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

        ServerHttpResponse originalResponse = exchange.getResponse();
        /*
         从原始的 ServerHttpResponse 对象中获取一个 DataBufferFactory 实例，来处理响应体的数据缓冲区。
         bufferFactory: DataBufferFactory 是一个工厂接口，用于创建 DataBuffer 对象。DataBuffer 是一种表示数据缓冲区的对象，
                        通常用于处理 I/O 操作，特别是在处理响应体和请求体的数据流时。它提供了对字节数据的低级处理能力。
         */
        DataBufferFactory bufferFactory = originalResponse.bufferFactory();

        // 创建一个装饰器以修改响应数据
        // 创建一个 ServerHttpResponseDecorator，用于装饰原始响应对象，以便在响应写入过程中对响应体进行修改
        // ServerHttpResponseDecorator 是一个用于装饰 ServerHttpResponse 的类，允许你在响应体写入前或写入过程中修改响应数据。
        // 通过重写 writeWith 方法，可以在响应体传输之前对其进行处理，如修改、加密、压缩等操作。
        ServerHttpResponseDecorator response = new ServerHttpResponseDecorator(originalResponse) {

            // 重写 writeWith 方法，允许对响应体的内容进行修改
            // 在正常情况下，Spring Gateway直接将服务端的响应传递给客户端。而通过重写writeWith方法，我们可以动态读取、处理或替换响应内容，
            // 然后再将修改后的响应数据发送给客户端。
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {

                // 检查响应的状态码是否为 200 OK，且响应体是 Flux（异步数据流）
                if (Objects.equals(getStatusCode(), HttpStatus.OK) && body instanceof Flux) {

                    // 获取响应的 Content-Type 属性，用于判断响应是否是 JSON 或文本格式
                    String originalResponseContentType = exchange.getAttribute(ORIGINAL_RESPONSE_CONTENT_TYPE_ATTR);

                    // 如果响应的 Content-Type 为 "application/json" 或 "text/plain"，则需要对响应体进行处理
                    if (StringUtils.isNotBlank(originalResponseContentType)
                            && (originalResponseContentType.contains("application/json")
                            || originalResponseContentType.contains("text/plain"))) {

                        // 将 body 转换为 Flux 数据流，Flux 是响应体的异步数据流
                        Flux<? extends DataBuffer> fluxBody = Flux.from(body);

                        // 对响应体进行修改，读取原始数据并进行拼接
                        return super.writeWith(fluxBody.buffer().map(dataBuffers -> {
                            List<String> list = new ArrayList<>();

                            // 遍历所有 DataBuffer，读取其内容，并将内容合并为字符串
                            dataBuffers.forEach(dataBuffer -> {
                                try {
                                    // 从 DataBuffer 中读取字节并转换为字符串
                                    byte[] content = new byte[dataBuffer.readableByteCount()];
                                    dataBuffer.read(content);
                                    // 释放 DataBuffer，避免内存泄漏
                                    DataBufferUtils.release(dataBuffer);

                                    // 将内容添加到列表中
                                    list.add(new String(content, StandardCharsets.UTF_8));
                                } catch (Exception e) {
                                    // 错误处理，打印异常堆栈
                                    log.error("读取响应体失败：{}", getStackTraceAsString(e));
                                }
                            });

                            // 合并所有部分的响应体内容
                            String responseData = String.join("", list); // 将各部分数据按空字符串拼接

                            // 修改响应体内容，这里直接将响应内容替换为 "fang"
                            responseData = "fang";

                            // 将修改后的响应体转换为字节数组并封装为 DataBuffer
                            byte[] modifiedContent = responseData.getBytes(StandardCharsets.UTF_8);

                            // 更新响应头中的 Content-Length
                            originalResponse.getHeaders().setContentLength(modifiedContent.length);

                            // 使用 bufferFactory 封装修改后的字节数组为 DataBuffer
                            return bufferFactory.wrap(modifiedContent);
                        }));
                    }
                }

                // 如果响应不符合处理条件，直接返回原始响应
                return super.writeWith(body);
            }

            // 重写 writeAndFlushWith 方法，以便处理更多复杂的响应体写入情形
            @Override
            public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
                // 使用 flatMapSequential 来将多个数据流合并处理
                return writeWith(Flux.from(body).flatMapSequential(p -> p));
            }
        };

        // 用装饰后的响应替换原始响应
        return chain.filter(exchange.mutate().response(response).build());
    }

    private String getStackTraceAsString(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }
}
