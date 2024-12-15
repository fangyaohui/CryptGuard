package com.crypt.cryptguard.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * @FileName EncryptResponseAspect
 * @Description 实现 @EncryptResponse 的逻辑，拦截特定方法的响应数据，对响应内容进行加密。
 * @Author yaoHui
 * @date 2024-12-15
 **/
@Aspect
@Component
public class EncryptResponseAspect {
}
