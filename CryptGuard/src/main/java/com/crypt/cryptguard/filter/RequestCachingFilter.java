package com.crypt.cryptguard.filter;


import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;

/**
 * @FileName RequestCachingFilter
 * @Description
 * @Author yaoHui
 * @date 2024-12-17
 **/
public class RequestCachingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        if(servletRequest instanceof HttpServletRequest){
            ContentCachingRequestWrapper contentCachingRequestWrapper =
                    new ContentCachingRequestWrapper((HttpServletRequest) servletRequest);
            filterChain.doFilter(contentCachingRequestWrapper,servletResponse);
        }else {
            filterChain.doFilter(servletRequest,servletResponse);
        }
    }
}
