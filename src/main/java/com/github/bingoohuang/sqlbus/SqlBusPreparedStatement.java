package com.github.bingoohuang.sqlbus;

import lombok.val;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static java.lang.reflect.Proxy.newProxyInstance;

/**
 * @author bingoohuang [bingoohuang@gmail.com] Created on 2016/10/13.
 */
public class SqlBusPreparedStatement implements InvocationHandler {
    final SqlBusConfig sqlBusConfig;
    final PreparedStatement statement;
    final String rawSql;
    final Map<Integer, Object> parameters = new TreeMap<Integer, Object>();
    final SqlAnatomy sqlAnatomy;

    private SqlBusPreparedStatement(
            SqlBusConfig sqlBusConfig,
            PreparedStatement statement,
            String sql) {
        this.sqlBusConfig = sqlBusConfig;
        this.statement = statement;
        this.rawSql = sql;
        this.sqlAnatomy = sqlBusConfig.getSqlAnatomy(rawSql);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        if (methodName.equals("setLong")
                || methodName.equals("setString")
                || methodName.equals("setInt")
                || methodName.equals("setObject")
                || methodName.equals("setTimestamp")) {
            parameters.put((Integer) args[0], args[1]);
        } else if (methodName.equals("executeUpdate")) {
            executeUpdate();
        }

        return method.invoke(statement, args);
    }

    private void executeUpdate() {
        SqlBusEvent sqlBusEvent = new SqlBusEvent(
                sqlAnatomy.getTable(),
                sqlAnatomy.getRawSqlType(),
                sqlAnatomy.getRawSql(),
                createParameters()
        );

        sqlBusConfig.post(sqlBusEvent);
        parameters.clear();

    }

    private List<Object> createParameters() {
        int size = parameters.size();
        val listParameters = new ArrayList<Object>(size);
        for (Map.Entry<Integer, Object> entry : parameters.entrySet()) {
            listParameters.add(entry.getValue());
        }

        return listParameters;
    }

    public static PreparedStatement proxy(
            SqlBusConfig sqlBusConfig,
            PreparedStatement statement,
            String sql) {
        val impl = new SqlBusPreparedStatement(sqlBusConfig, statement, sql);
        return (PreparedStatement) newProxyInstance(
                impl.getClass().getClassLoader(),
                new Class<?>[]{PreparedStatement.class}, impl);
    }
}
