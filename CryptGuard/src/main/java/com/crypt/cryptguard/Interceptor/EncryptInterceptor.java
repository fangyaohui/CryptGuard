package com.crypt.cryptguard.Interceptor;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.PreparedStatement;
import java.sql.Statement;

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
public class EncryptInterceptor implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        log.info("intercept is running...");
        return invocation.proceed();
    }
}
