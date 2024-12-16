package com.crypt.cryptguard.annotation;

import java.lang.annotation.*;

/**
 * @FileName DecryptController
 * @Description 标记在 `Controller` 上，自动对所有请求的 **URL** 和/或 **参数** 进行解密，可通过属性指定解密范围。
 * @Author yaoHui
 * @date 2024-12-15
 **/
@Target(value = ElementType.TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
public @interface DecryptController {

}
