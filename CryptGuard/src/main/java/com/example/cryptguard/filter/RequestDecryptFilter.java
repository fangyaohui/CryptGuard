package com.example.cryptguard.filter;


import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @FileName RequestDecryptFilter
 * @Description 设置过滤器使得网关层对所有指定的请求进行解密
 * @Author yaoHui
 * @date 2024-12-04
 **/
@Slf4j
@WebFilter(filterName = "RequestDecryptFilter")
public class RequestDecryptFilter implements Filter {

    /**
     * @Author yaoHui
     * @Date 2024/12/4
     * @Description 如果你需要在过滤器创建时执行任何初始化操作（如：读取配置文件、初始化资源、数据库连接等），你应该在 init() 方法中进行这些操作。
     * 该方法仅在过滤器实例化时调用一次，适用于资源的初始化。
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
        log.info("RequestDecryptFilter init");
    }

    /**
     * @Author yaoHui
     * @Date 2024/12/4
     * @Description doFilter() 是过滤器的核心方法，它会对每个请求执行实际的过滤操作。该方法会在请求到达目标资源之前执行，
     * 并可以决定是否将请求交给后续过滤器或目标资源进行处理。
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        log.info("RequestDecryptFilter doFilter running");
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String originalUrl = request.getRequestURI();
        log.info("RequestDecryptFilter original url is " + originalUrl);
        String decryptedUrl =  "/decryptedUrl";

        HttpServletRequestWrapper requestWrapper = new HttpServletRequestWrapper(request){
            @Override
            public String getRequestURI(){
                return decryptedUrl;
            }
        };


        log.info("RequestDecryptFilter doFilter end");
        filterChain.doFilter(requestWrapper,servletResponse);
    }

    /***
     * @Author yaoHui
     * @Date 2024/12/4
     * @Description destroy() 方法是在过滤器被销毁之前调用，用于释放过滤器在 init() 方法中分配的资源。通常，
     * 在容器关闭或者过滤器不再使用时，destroy() 会被调用。
     */
    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
