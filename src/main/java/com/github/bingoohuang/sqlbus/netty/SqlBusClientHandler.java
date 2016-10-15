package com.github.bingoohuang.sqlbus.netty;

import com.github.bingoohuang.sqlbus.proto.SqlBusMsg.SqlBusEventMsgRsp;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author bingoohuang [bingoohuang@gmail.com] Created on 2016/10/15.
 */
@Slf4j
public class SqlBusClientHandler extends SimpleChannelInboundHandler<SqlBusEventMsgRsp> {
    private final SqlBusNettyConfig sqlBusNettyConfig;
    private final SqlBusClient sqlBusClient;
    long startTime = -1;

    public SqlBusClientHandler(SqlBusNettyConfig sqlBusNettyConfig, SqlBusClient sqlBusClient) {
        super(false);
        this.sqlBusNettyConfig = sqlBusNettyConfig;
        this.sqlBusClient = sqlBusClient;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, SqlBusEventMsgRsp rsp
    ) throws Exception {
        System.out.println(rsp);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        if (startTime < 0) startTime = System.currentTimeMillis();
        println("Connected to: " + ctx.channel().remoteAddress());

        Channel channel = ctx.channel();
        sqlBusClient.setChannel(channel);
    }

    @Override
    public void channelUnregistered(final ChannelHandlerContext ctx) throws Exception {
        sqlBusClient.setChannel(null);
        reconnect(ctx.channel().eventLoop());
    }

    public void reconnect(final EventLoop loop) {
        println("Sleeping for: " + sqlBusNettyConfig.getReconnectDelay() + 's');

        loop.schedule(new Runnable() {
            @Override
            public void run() {
                println("Reconnecting to: " + sqlBusNettyConfig.getHostPort());
                sqlBusClient.connect();
            }
        }, sqlBusNettyConfig.getReconnectDelay(), TimeUnit.SECONDS);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Disconnected from: " + ctx.channel().remoteAddress());
        sqlBusClient.setChannel(null);

        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    void println(String msg) {
        if (startTime < 0) {
            log.info("[SERVER IS DOWN] {}", msg);
        } else {
            long seconds = (System.currentTimeMillis() - startTime) / 1000;
            log.info("[UPTIME: {}] {}", seconds, msg);
        }
    }
}
