package com.crypt.cryptguard.aspect;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.BufferedReader;
import java.util.stream.Collectors;

/**
 * @FileName DecryptRequestAspect
 * @Description 实现 @DecryptRequest 的逻辑，拦截特定方法的请求，对指定范围（URL 和/或参数）进行解密。
 * @Author yaoHui
 * @date 2024-12-15
 **/
@Slf4j
@Aspect
@Component
public class DecryptRequestAspect {

    @Pointcut("@annotation(com.crypt.cryptguard.annotation.DecryptRequest)")
    public void decryptRequestPointCut(){

    }

    @Before("decryptRequestPointCut()")
    public void handleDecryptRequestPointCut(JoinPoint joinPoint) throws Throwable {

        ServletRequestAttributes servletRequestAttributes =
                (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();

        if(ObjectUtils.isEmpty(servletRequestAttributes)){
            log.info("decryptRequestPointCut ServletRequestAttributes is null");
            return ;
        }

        HttpServletRequest httpServletRequest = servletRequestAttributes.getRequest();

        if(!(httpServletRequest instanceof HttpServletRequestWrapper)){
            log.info("Request is not wrapped in ContentCachingRequestWrapper");
            return ;
        }

        ContentCachingRequestWrapper wrapperRequest = (ContentCachingRequestWrapper) httpServletRequest;

        String originalBody = new String(wrapperRequest.getContentAsByteArray(), wrapperRequest.getCharacterEncoding());

//        // 读取原始请求体
//        String requestBody = new BufferedReader(request.getReader())
//                .lines()
//                .collect(Collectors.joining(System.lineSeparator()));

        log.info("Original Request Body:{}",originalBody);

        Object[] args = joinPoint.getArgs();

        log.info("doDecryptRequestPointCut is running");

    }


}
