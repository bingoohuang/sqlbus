package com.github.bingoohuang.sqlbus.impl;

import com.github.bingoohuang.sqlbus.netty.SqlBusClient;
import com.github.bingoohuang.sqlbus.proto.SqlBusMsg;
import com.github.bingoohuang.sqlbus.utils.SqlBusUtils;
import com.google.common.eventbus.Subscribe;
import lombok.AllArgsConstructor;
import lombok.val;

/**
 * @author bingoohuang [bingoohuang@gmail.com] Created on 2016/10/14.
 */
@AllArgsConstructor
public class SqlBusRemoteWorker {
    final SqlBusClient sqlBusClient;

    @Subscribe
    public void sendToRemote(SqlBusEvent sqlBusEvent) {
        val builder = SqlBusMsg.SqlBusEventMsgReq.newBuilder()
                .setHostname(SqlBusUtils.getHostname())
                .setTimestamp(System.currentTimeMillis())
                .setTable(sqlBusEvent.getTable())
                .setSqlType(sqlBusEvent.getSqlType().toString())
                .setSql(sqlBusEvent.getSql());

        val parameters = sqlBusEvent.getParameters();
        if (parameters != null) {
            for (Object p : parameters) {
                builder.addParameters(p == null ? null : p.toString());
            }
        }

        SqlBusMsg.SqlBusEventMsgReq msg = builder.build();
        sqlBusClient.send(msg);
    }
}
