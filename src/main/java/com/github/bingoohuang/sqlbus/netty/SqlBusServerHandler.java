package com.github.bingoohuang.sqlbus.netty;

import com.github.bingoohuang.sqlbus.proto.SqlBusMsg.SqlBusEventMsgReq;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author bingoohuang [bingoohuang@gmail.com] Created on 2016/10/15.
 */
public class SqlBusServerHandler extends SimpleChannelInboundHandler<SqlBusEventMsgReq> {
    private final SqlBusMsgHandler sqlBusMsgHandler;

    public SqlBusServerHandler(SqlBusMsgHandler sqlBusMsgHandler) {
        this.sqlBusMsgHandler = sqlBusMsgHandler;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, SqlBusEventMsgReq req) throws Exception {
        sqlBusMsgHandler.handle(req, ctx);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}