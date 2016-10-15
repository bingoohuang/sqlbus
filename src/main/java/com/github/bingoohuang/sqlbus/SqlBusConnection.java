package com.github.bingoohuang.sqlbus;

import com.github.bingoohuang.sqlbus.impl.SqlBusPreparedStatement;
import com.google.common.reflect.Reflection;
import lombok.val;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * @author bingoohuang [bingoohuang@gmail.com] Created on 2016/10/13.
 */
public class SqlBusConnection implements InvocationHandler {
    private final SqlBusConfig sqlBusConfig;
    private final Connection connection;

    private SqlBusConnection(
            SqlBusConfig sqlBusConfig, Connection connection) {
        this.sqlBusConfig = sqlBusConfig;
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

            if (sqlBusConfig.isCaredSql(sql)) {
                val ps = (PreparedStatement) method.invoke(connection, args);
                return SqlBusPreparedStatement.proxy(sqlBusConfig, ps, sql);
            }
        }

        val invoke = method.invoke(connection, args);

        return invoke;
    }

    public static Connection proxy(
            SqlBusConfig sqlBusConfig, Connection connection) {
        val impl = new SqlBusConnection(sqlBusConfig, connection);
        return Reflection.newProxy(Connection.class, impl);
    }

}
