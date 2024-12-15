package com.crypt.cryptguard.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * @FileName DecryptRequestAspect
 * @Description 实现 @DecryptRequest 的逻辑，拦截特定方法的请求，对指定范围（URL 和/或参数）进行解密。
 * @Author yaoHui
 * @date 2024-12-15
 **/
@Aspect
@Component
public class DecryptRequestAspect {
}
