package com.crypt.cryptguard.annotation;

import java.lang.annotation.*;

/**
 * @FileName EncryptResponse
 * @Description 标记在方法上，对当前方法的响应数据进行加密后返回。
 * @Author yaoHui
 * @date 2024-12-15
 **/
@Target(value = ElementType.TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
public @interface EncryptResponse {
}
