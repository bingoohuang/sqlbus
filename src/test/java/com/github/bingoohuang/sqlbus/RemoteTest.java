package com.github.bingoohuang.sqlbus;

import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.n3r.idworker.Id;

import java.sql.Connection;
import java.sql.Timestamp;

/**
 * @author bingoohuang [bingoohuang@gmail.com] Created on 2016/10/15.
 */
public class RemoteTest {
    static SqlBusConfig sqlBusConfig;
    static Connection conn;

    @BeforeClass @SneakyThrows
    public static void beforeClass() {
        sqlBusConfig = SqlBusConfigBuilder.newBuilder()
                .care("T_ORDER1", SqlType.UPDATE)
                .care("T_IGNORE1", SqlType.INSERT)
                .build();

        Connection connImpl = JdbcUtils.createConnection();

        @Cleanup val statement = connImpl.createStatement();
        JdbcUtils.executeSql(statement, "CREATE TABLE T_ORDER1(ORDER_ID NUMBER NOT NULL, CREATE_TIME TIMESTAMP(6) NOT NULL, ORDER_TYPE VARCHAR2(10) NOT NULL, PRIMARY KEY(ORDER_ID))");
        JdbcUtils.executeSql(statement, "CREATE TABLE T_IGNORE1(STH VARCHAR2(10) NOT NULL)");

        conn = SqlBusConnection.proxy(sqlBusConfig, connImpl);
    }

    @AfterClass @SneakyThrows
    public static void afterClass() {
        @Cleanup val statement = conn.createStatement();
        JdbcUtils.executeSql(statement, "DROP TABLE T_ORDER1");
        JdbcUtils.executeSql(statement, "DROP TABLE T_IGNORE1");

        conn.close();

        sqlBusConfig.close();
    }

    @Test @SneakyThrows
    public void test1() {
        val insertSql = "insert into t_order1(order_id, create_time, order_type) values(?, ?, ?)";
        @Cleanup val psInsert = conn.prepareStatement(insertSql);
        long orderId = Id.next();

        psInsert.setLong(1, orderId);
        Timestamp timestamp = JdbcUtils.currentTimestamp();
        psInsert.setTimestamp(2, timestamp);
        String randomType = JdbcUtils.createRandomType();
        psInsert.setString(3, randomType);
        psInsert.executeUpdate();


        val updateSql = "update T_order1 set order_type = ? where order_id = ?";
        @Cleanup val psUpdate = conn.prepareStatement(updateSql);

        String randomType1 = JdbcUtils.createRandomType();
        psUpdate.setString(1, randomType1);
        psUpdate.setLong(2, orderId);
        psUpdate.executeUpdate();
    }

}
