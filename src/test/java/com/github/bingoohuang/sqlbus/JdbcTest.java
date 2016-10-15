package com.github.bingoohuang.sqlbus;

import com.github.bingoohuang.sqlbus.impl.SqlBusEvent;
import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.n3r.idworker.Id;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.Queue;

import static com.google.common.truth.Truth.assertThat;

/**
 * @author bingoohuang [bingoohuang@gmail.com] Created on 2016/10/13.
 */
public class JdbcTest {
    static EventBus eventBus = new EventBus();
    static SqlBusConfig sqlBusConfig;
    static Connection conn;

    @BeforeClass @SneakyThrows
    public static void beforeClass() {
        sqlBusConfig = SqlBusConfigBuilder.newBuilder()
                .care("T_ORDER", SqlType.INSERT, SqlType.UPDATE)
                .care("T_IGNORE", SqlType.INSERT)
                .eventbus(eventBus)
                .build();

        Connection connImpl = JdbcUtils.createConnection();

        @Cleanup val statement = connImpl.createStatement();
        JdbcUtils.executeSql(statement, "CREATE TABLE T_ORDER(ORDER_ID NUMBER NOT NULL, CREATE_TIME TIMESTAMP(6) NOT NULL, ORDER_TYPE VARCHAR2(10) NOT NULL, PRIMARY KEY(ORDER_ID))");
        JdbcUtils.executeSql(statement, "CREATE TABLE T_IGNORE(STH VARCHAR2(10) NOT NULL)");

        conn = SqlBusConnection.proxy(sqlBusConfig, connImpl);
    }

    @AfterClass @SneakyThrows
    public static void afterClass() {
        @Cleanup val statement = conn.createStatement();
        JdbcUtils.executeSql(statement, "DROP TABLE T_ORDER");
        JdbcUtils.executeSql(statement, "DROP TABLE T_IGNORE");

        conn.close();
    }

    static class EventSubscribe implements Closeable {
        private final Queue<SqlBusEvent> queue;
        private final EventBus eventBus;

        public EventSubscribe(EventBus eventBus, Queue<SqlBusEvent> queue) {
            this.eventBus = eventBus;
            this.queue = queue;
        }

        @Subscribe
        public void subscribe(SqlBusEvent sqlBusEvent) {
            queue.add(sqlBusEvent);
        }

        @Override public void close() throws IOException {
            eventBus.unregister(this);
        }
    }

    @Test @SneakyThrows
    public void test1() {
        val queue = new LinkedList<SqlBusEvent>();
        @Cleanup val eventSubscribe = new EventSubscribe(eventBus, queue);
        eventBus.register(eventSubscribe);

        val insertSql = "insert into t_order(order_id, create_time, order_type) values(?, ?, ?)";
        @Cleanup val psInsert = conn.prepareStatement(insertSql);
        long orderId = Id.next();

        psInsert.setLong(1, orderId);
        Timestamp timestamp = JdbcUtils.currentTimestamp();
        psInsert.setTimestamp(2, timestamp);
        String randomType = JdbcUtils.createRandomType();
        psInsert.setString(3, randomType);
        psInsert.executeUpdate();

        SqlBusEvent sqlBusEvent = queue.pollLast();
        assertThat(sqlBusEvent.getSql()).isEqualTo(insertSql);
        assertThat(sqlBusEvent.getSqlType()).isEqualTo(SqlType.INSERT);
        assertThat(sqlBusEvent.getTable()).isEqualTo("t_order");
        assertThat(sqlBusEvent.getParameters()).isEqualTo(Lists.newArrayList(orderId, timestamp, randomType));
        assertThat(queue.pollLast()).isNull();


        val updateSql = "update T_order set order_type = ? where order_id = ?";
        @Cleanup val psUpdate = conn.prepareStatement(updateSql);

        String randomType1 = JdbcUtils.createRandomType();
        psUpdate.setString(1, randomType1);
        psUpdate.setLong(2, orderId);
        psUpdate.executeUpdate();

        sqlBusEvent = queue.pollLast();
        assertThat(sqlBusEvent.getSql()).isEqualTo(updateSql);
        assertThat(sqlBusEvent.getSqlType()).isEqualTo(SqlType.UPDATE);
        assertThat(sqlBusEvent.getTable()).isEqualTo("T_order");
        assertThat(sqlBusEvent.getParameters()).isEqualTo(Lists.newArrayList(randomType1, orderId));
        assertThat(queue.pollLast()).isNull();
    }

}
