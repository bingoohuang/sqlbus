package com.github.bingoohuang.sqlbus;

import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.Test;
import org.n3r.idworker.Id;

import java.sql.Timestamp;

import static java.sql.DriverManager.getConnection;
import static java.sql.DriverManager.registerDriver;

/**
 * @author bingoohuang [bingoohuang@gmail.com] Created on 2016/10/13.
 */
public class JdbcTest {
    @Test @SneakyThrows
    public void test1() {
        registerDriver(new oracle.jdbc.driver.OracleDriver());
        val url = "jdbc:oracle:thin:@192.168.99.100:49161:xe";
        val user = "system";
        val password = "oracle";
        val conn = getConnection(url, user, password);

        @Cleanup val connProxy = new ConnectionImpl(conn).createProxy();
        val sql = "insert into t_order(order_id, create_time, order_type)" +
                " values(?, ?, ?)";
        @Cleanup val ps = connProxy.prepareStatement(sql);
        ps.setString(3, "TEST");
        ps.setLong(1, Id.next());
        ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));

        ps.executeUpdate();
    }
}
