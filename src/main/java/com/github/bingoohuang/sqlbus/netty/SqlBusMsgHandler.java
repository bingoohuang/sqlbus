package com.github.bingoohuang.sqlbus.netty;

import com.github.bingoohuang.sqlbus.proto.SqlBusMsg.SqlBusEventMsgReq;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author bingoohuang [bingoohuang@gmail.com] Created on 2016/10/15.
 */
public interface SqlBusMsgHandler {
    void handle(SqlBusEventMsgReq sqlBusMsg, ChannelHandlerContext ctx);
}
