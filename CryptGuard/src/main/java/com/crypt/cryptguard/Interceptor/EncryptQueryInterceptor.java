package com.crypt.cryptguard.Interceptor;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.aop.TargetSource;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * @FileName EncryptQueryInterceptor
 * @Description
 * @Author yaoHui
 * @date 2025-01-12
 **/

@Slf4j
@Intercepts({
//        @Signature(type = Executor.class,method = "query",args = {Object.class}),
        @Signature(type = Executor.class,
        method = "query",
        args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
})
public class EncryptQueryInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        log.info("intercept is running");
        Object[] objects = invocation.getArgs();
        MappedStatement mappedStatement = (MappedStatement) objects[0];

        if (objects.length < 2 || objects[1] == null){
            return invocation.proceed();
        }

        String namespace = mappedStatement.getId();

        Object parameterObject = objects[1];
        if (parameterObject instanceof MapperMethod.ParamMap<?> paramMap){
            paramMap.forEach((k,v) -> {
                if (v instanceof LambdaQueryWrapper<?> lambdaQueryWrapper){
                    String str = lambdaQueryWrapper.getEntity().toString();
                }
            });
        }


        return invocation.proceed();
    }
}
