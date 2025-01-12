package com.crypt.cryptguard.Interceptor;

import com.crypt.cryptguard.annotation.CryptTransient;
import com.crypt.cryptguard.strategy.CryptStrategy;
import com.crypt.cryptguard.strategy.CryptStrategyFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Field;

/**
 * @FileName EncryptInterceptor
 * @Description
 * @Author yaoHui
 * @date 2025-01-09
 **/
//@Intercepts({
//        @Signature(type = ParameterHandler.class,
//                method = "setParameters",
//                args = {PreparedStatement.class}),
//})
@Intercepts({
//        @Signature(type = Executor.class,
//                method = "query",
//                args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(
                type = Executor.class,
                method = "update",
                args = {MappedStatement.class, Object.class}
        ),
})
@Slf4j
public class EncryptUpdateInterceptor implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        log.info("intercept is running...");
        Object[] args = invocation.getArgs();
        Object parameterObject = args[1];
        if(ObjectUtils.isEmpty(parameterObject)){
            return invocation.proceed();
        }

        Class<?> clazz = parameterObject.getClass();
        Field[] fields = clazz.getDeclaredFields();

        // 判断当前更新的数据属性中是否存在指定注解
        for(Field field : fields){
            field.setAccessible(true);
            if(!field.isAnnotationPresent(CryptTransient.class)){
                continue;
            }

            CryptTransient cryptTransient = field.getAnnotation(CryptTransient.class);
            CryptStrategy cryptStrategy = CryptStrategyFactory.getStrategy(cryptTransient.strategy());
            String value = (String) field.get(parameterObject);
            if (ObjectUtils.isEmpty(value)){
                log.info("value is null... continue....");
                continue;
            }
            log.info("original value is "+value);
            String encryptedValue = cryptStrategy.encrypt(value);
            log.info("encrypted value is " + encryptedValue);
            field.set(parameterObject,encryptedValue);
        }

        log.info("encrypted Object is "+ parameterObject.toString());

        return invocation.proceed();
    }
}
