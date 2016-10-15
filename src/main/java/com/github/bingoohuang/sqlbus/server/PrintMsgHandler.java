package com.github.bingoohuang.sqlbus.server;

import com.github.bingoohuang.sqlbus.netty.SqlBusMsgHandler;
import com.github.bingoohuang.sqlbus.proto.SqlBusMsg.SqlBusEventMsgReq;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author bingoohuang [bingoohuang@gmail.com] Created on 2016/10/15.
 */
public class PrintMsgHandler implements SqlBusMsgHandler {
    @Override
    public void handle(SqlBusEventMsgReq req, ChannelHandlerContext ctx) {
        System.out.println(req);
    }
}
