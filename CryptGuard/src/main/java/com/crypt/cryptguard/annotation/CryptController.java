package com.crypt.cryptguard.annotation;

import java.lang.annotation.*;

/**
 * @FileName CryptController
 * @Description 标记在 Controller 上，同时对所有请求的 URL、参数进行解密，且对响应进行加密。
 * @Author yaoHui
 * @date 2024-12-15
 **/
@Target(value = ElementType.TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
@DecryptController
@EncryptController
public @interface CryptController {
    boolean decryptUrl() default true; // 是否对 URL 解密
    boolean decryptParams() default true; // 是否对参数解密
}
