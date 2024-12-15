package com.crypt.cryptguard.annotation;

import java.lang.annotation.*;

/**
 * @FileName EncryptController
 * @Description 标记在 Controller 上，自动对所有请求的响应进行加密后返回。
 * @Author yaoHui
 * @date 2024-12-15
 **/
@Target(value = ElementType.TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
public @interface EncryptController {
}
