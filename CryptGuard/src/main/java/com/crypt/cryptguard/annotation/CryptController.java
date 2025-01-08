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
public @interface CryptController {
    // 是否解密整个参数（即包括所有字段的值）
    // 如果是False则是只会对value进行加密
    boolean allParamsDecrypt() default true;
}
