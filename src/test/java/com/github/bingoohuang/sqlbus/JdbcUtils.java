package com.github.bingoohuang.sqlbus;

import lombok.experimental.UtilityClass;
import lombok.val;
import org.apache.commons.lang3.RandomStringUtils;

import java.sql.*;

/**
 * @author bingoohuang [bingoohuang@gmail.com] Created on 2016/10/15.
 */
@UtilityClass
public class JdbcUtils {
    public Connection createConnection() throws SQLException {
        DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
        val url = "jdbc:oracle:thin:@192.168.99.100:49161:xe";
        val user = "system";
        val password = "oracle";
        return DriverManager.getConnection(url, user, password);
    }

    public void executeSql(Statement statement, String sql) {
        try {
            statement.execute(sql);
        } catch (SQLException e) {
            // ignore
        }
    }

    public Timestamp currentTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }

    public String createRandomType() {
        return RandomStringUtils.random(10,
                "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456790");
    }
}
