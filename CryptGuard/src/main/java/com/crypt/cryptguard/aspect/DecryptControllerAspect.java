package com.crypt.cryptguard.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * @FileName DecryptControllerAspect
 * @Description 实现 @DecryptController 的逻辑，在 Controller 方法执行前拦截请求，对 URL 和/或参数进行解密。
 * @Author yaoHui
 * @date 2024-12-15
 **/
@Aspect
@Component
public class DecryptControllerAspect {
}
