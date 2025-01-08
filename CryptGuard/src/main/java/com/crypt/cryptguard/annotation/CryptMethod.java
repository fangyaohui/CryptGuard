package com.crypt.cryptguard.annotation;

import java.lang.annotation.*;

/**
 * @FileName CryptMethod
 * @Description 标记在方法上，同时对请求（URL 和参数）进行解密，对响应进行加密。
 * @Author yaoHui
 * @date 2024-12-15
 **/
@Target(value = ElementType.METHOD)
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
public @interface CryptMethod {

    // 是否解密整个参数（即包括所有字段的值）
    // 如果是False则是只会对value进行加密
    boolean allParamsDecrypt() default true;

}
