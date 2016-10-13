package com.github.bingoohuang.sqlbus;

import lombok.val;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * @author bingoohuang [bingoohuang@gmail.com] Created on 2016/10/13.
 */
public class ConnectionImpl implements InvocationHandler {
    private final Connection connection;

    public ConnectionImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Object invoke(
            Object proxy,
            Method method,
            Object[] args) throws Throwable {
        val methodName = method.getName();
        if (methodName.equals("prepareStatement")) {
            val sql = (String) args[0];
            if (isCaredSql(sql)) {
                val ps = (PreparedStatement) method.invoke(connection, args);
                return new PreparedStatementImpl(ps, sql).createProxy();
            }
        }

        val invoke = method.invoke(connection, args);

        return invoke;
    }

    public Connection createProxy() {
        return (Connection) Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class<?>[]{Connection.class}, this);
    }

    // 是否是受关注的SQL语句
    private boolean isCaredSql(String sql) {
        return SqlCaringCache.isCaredSql(sql);
    }
}
