package com.github.bingoohuang.sqlbus.netty;

import com.github.bingoohuang.sqlbus.proto.SqlBusMsg;
import com.github.bingoohuang.sqlbus.proto.SqlBusMsg.SqlBusEventMsgReq;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.ssl.SslContext;

import java.io.Closeable;

/**
 * @author bingoohuang [bingoohuang@gmail.com] Created on 2016/10/15.
 */
public class SqlBusClient implements Closeable {
    EventLoopGroup eventLoop = new NioEventLoopGroup(1);
    volatile Channel channel;
    SqlBusClientHandler clientHandler;

    private final SqlBusNettyConfig sqlBusNettyConfig;
    private final SslContext sslCtx;

    public SqlBusClient(SqlBusNettyConfig sqlBusNettyConfig) {
        this.sqlBusNettyConfig = sqlBusNettyConfig;
        this.sslCtx = sqlBusNettyConfig.configureSslForClient();
    }

    public void send(SqlBusEventMsgReq req) {
        if (channel != null) channel.writeAndFlush(req);
    }

    private Bootstrap configureBootstrap() {
        Bootstrap b = new Bootstrap();
        b.group(eventLoop);
        b.channel(NioSocketChannel.class);
        b.option(ChannelOption.SO_KEEPALIVE, true);
        b.remoteAddress(sqlBusNettyConfig.getHost(), sqlBusNettyConfig.getPort());
        clientHandler = new SqlBusClientHandler(sqlBusNettyConfig, SqlBusClient.this);

        b.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) {
                ChannelPipeline p = ch.pipeline();
                if (sslCtx != null)
                    p.addLast(sslCtx.newHandler(ch.alloc(),
                            sqlBusNettyConfig.getHost(),
                            sqlBusNettyConfig.getPort()));

                p.addLast(new ProtobufVarint32FrameDecoder());
                p.addLast(new ProtobufDecoder(SqlBusMsg.SqlBusEventMsgRsp.getDefaultInstance()));

                p.addLast(new ProtobufVarint32LengthFieldPrepender());
                p.addLast(new ProtobufEncoder());

                p.addLast(clientHandler);
            }
        });
        return b;
    }

    public void connect() {
        Bootstrap bootstrap = configureBootstrap();
        bootstrap.connect();
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    @Override public void close() {
        this.channel.close();
    }
}
