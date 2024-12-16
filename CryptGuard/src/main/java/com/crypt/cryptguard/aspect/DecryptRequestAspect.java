package com.crypt.cryptguard.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

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
    public void doDecryptRequestPointCut(JoinPoint joinPoint) throws Throwable {

        Object[] args = joinPoint.getArgs();

        log.info("doDecryptRequestPointCut is running");
    }


}
