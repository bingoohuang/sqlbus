package com.github.bingoohuang.sqlbus;

import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.n3r.idworker.Id;

import java.sql.Timestamp;

import static java.sql.DriverManager.getConnection;
import static java.sql.DriverManager.registerDriver;

/**
 * @author bingoohuang [bingoohuang@gmail.com] Created on 2016/10/13.
 */
public class JdbcTest {
    @BeforeClass
    public static void beforeClass() {
        SqlBusConfig.care("t_order", RawSqlType.INSERT, RawSqlType.UPDATE);
    }

    @Test @SneakyThrows
    public void test1() {
        registerDriver(new oracle.jdbc.driver.OracleDriver());
        val url = "jdbc:oracle:thin:@192.168.99.100:49161:xe";
        val user = "system";
        val password = "oracle";
        val conn = getConnection(url, user, password);

        /*
         DROP TABLE T_ORDER;
         CREATE TABLE T_ORDER(ORDER_ID NUMBER NOT NULL, CREATE_TIME TIMESTAMP(6) NOT NULL, ORDER_TYPE VARCHAR2(10) NOT NULL, PRIMARY KEY(ORDER_ID));
        */
        @Cleanup val connProxy = new ConnectionImpl(conn).createProxy();
        val insertSql = "insert into t_order(order_id, create_time, order_type)" +
                " values(?, ?, ?)";
        @Cleanup val psInsert = connProxy.prepareStatement(insertSql);
        psInsert.setString(3, RandomStringUtils.random(10,
                "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456790"));
        long orderId = Id.next();
        psInsert.setLong(1, orderId);
        psInsert.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
        psInsert.executeUpdate();


        String updateSql = "update t_order set order_type = ? where order_id = ?";
        @Cleanup val psUpdate = connProxy.prepareStatement(updateSql);
        psUpdate.setString(1, RandomStringUtils.random(10,
                "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456790"));
        psUpdate.setLong(2, orderId);
        psUpdate.executeUpdate();

    }
}
