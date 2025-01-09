package com.crypt.cryptguard.annotation;

import java.lang.annotation.*;

/**
 * @FileName CryptTransient
 * @Description
 *  *  * - **作用范围**: 类或属性
 *  *  * - **作用描述**: 用于标记对类的所有属性或单个属性进行加密和解密，但这些解密只作用于接口传输过程中。
 *  该注解标记则表示对该类或者该属性即需要进行加密也需要进行解密
 * @Author yaoHui
 * @date 2024-12-21
 **/
@Target({ElementType.FIELD,ElementType.TYPE})
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
public @interface CryptTransient {

    String strategy() default "AES"; // 加密算法，默认AES
    boolean querySupport() default false; // 是否支持普通查询
    boolean fuzzyQuerySupport() default false; // 是否支持模糊查询

}
