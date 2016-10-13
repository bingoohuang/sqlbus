package com.github.bingoohuang.sqlbus;

import com.alibaba.fastjson.JSON;
import lombok.val;
import org.hjson.JsonValue;
import org.hjson.Stringify;

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
public class PreparedStatementImpl implements InvocationHandler {
    final PreparedStatement statement;
    final String rawSql;
    final Map<Integer, Object> parameters = new TreeMap<Integer, Object>();
    final SqlAnatomy sqlAnatomy;

    public PreparedStatementImpl(
            PreparedStatement statement,
            String sql) {
        this.statement = statement;
        this.rawSql = sql;
        this.sqlAnatomy = SqlCaringCache.getSqlAnatomy(rawSql);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        if (methodName.equals("setLong")
                || methodName.equals("setString")
                || methodName.equals("setObject")
                || methodName.equals("setTimestamp")) {
            parameters.put((Integer) args[0], args[1]);
        } else if (methodName.equals("executeUpdate")) {
            executeUpdate();
        }

        return method.invoke(statement, args);
    }

    private void executeUpdate() {
        sqlAnatomy.setParameters(createParameters());
        String json = JSON.toJSONString(sqlAnatomy);
        parameters.clear();

        String hjson = JsonValue.readHjson(json).toString(Stringify.HJSON);
        System.out.println(hjson);
    }

    private List<String> createParameters() {
        int size = parameters.size();
        val listParameters = new ArrayList<String>(size);
        for (Map.Entry<Integer, Object> entry : parameters.entrySet()) {
            listParameters.add(entry.getValue().toString());
        }

        return listParameters;
    }

    public PreparedStatement createProxy() {
        return (PreparedStatement) newProxyInstance(getClass().getClassLoader(),
                new Class<?>[]{PreparedStatement.class}, this);
    }
}
