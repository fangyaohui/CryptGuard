package com.crypt.cryptguard.annotation;

import java.lang.annotation.*;

/**
 * @FileName CryptPersistent
 * @Description
 *  *  * - **作用范围**: 类或属性
 *  *  * - **作用描述**: 用于标记对类的所有属性或单个属性从数据库读取时会进行加密解密。
 * @Author yaoHui
 * @date 2024-12-21
 **/
@Target({ElementType.FIELD,ElementType.TYPE})
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
public @interface CryptPersistent {
    String strategy() default ""; // 加密算法，默认AES
    boolean querySupport() default false; // 是否支持普通查询
    boolean fuzzyQuerySupport() default false; // 是否支持模糊查询
}
