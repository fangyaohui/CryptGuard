package com.crypt.cryptguard.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * @FileName EncryptControllerAspect
 * @Description 实现 @EncryptController 的逻辑，在 Controller 方法执行后拦截响应数据，对响应数据进行加密。
 * @Author yaoHui
 * @date 2024-12-15
 **/
@Aspect
@Component
public class EncryptControllerAspect {
}
