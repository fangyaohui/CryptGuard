package com.crypt.cryptguard.annotation;

import java.lang.annotation.*;

/**
 * @FileName CryptMethod
 * @Description 标记在方法上，同时对请求（URL 和参数）进行解密，对响应进行加密。
 * @Author yaoHui
 * @date 2024-12-15
 **/
@Target(value = ElementType.TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
@DecryptRequest
@EncryptResponse
public @interface CryptMethod {
    boolean decryptUrl() default true; // 是否对 URL 解密
    boolean decryptParams() default true; // 是否对参数解密
}
