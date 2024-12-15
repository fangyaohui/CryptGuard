package com.crypt.cryptguard.annotation;

import java.lang.annotation.*;

/**
 * @FileName DecryptRequest
 * @Description 标记在方法上，针对当前方法的请求参数和/或 URL 进行解密（可通过属性指定范围）。
 * @Author yaoHui
 * @date 2024-12-15
 **/
@Target(value = ElementType.TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
public @interface DecryptRequest {
    boolean decryptUrl() default true;
    boolean decryptParams() default true;
}
