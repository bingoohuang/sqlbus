package com.github.bingoohuang.sqlbus.netty;

import com.github.bingoohuang.sqlbus.proto.SqlBusMsg.SqlBusEventMsgReq;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import lombok.SneakyThrows;
import lombok.val;

/**
 * @author bingoohuang [bingoohuang@gmail.com] Created on 2016/10/15.
 */
public class SqlBusServer {
    private final SslContext sslCtx;
    private final SqlBusNettyConfig sqlBusNettyConfig;

    public SqlBusServer(SqlBusNettyConfig sqlBusNettyConfig) {
        this.sqlBusNettyConfig = sqlBusNettyConfig;
        this.sslCtx = sqlBusNettyConfig.configureSslForServer();
    }

    @SneakyThrows
    public void startup(final SqlBusMsgHandler msgHandler) {
        val bossGroup = new NioEventLoopGroup(1);
        val workerGroup = new NioEventLoopGroup();
        try {
            val b = new ServerBootstrap();
            b.group(bossGroup, workerGroup);
            b.channel(NioServerSocketChannel.class);
            b.handler(new LoggingHandler(LogLevel.INFO));
            b.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline p = ch.pipeline();
                    if (sslCtx != null)
                        p.addLast(sslCtx.newHandler(ch.alloc()));

                    p.addLast(new ProtobufVarint32FrameDecoder());
                    p.addLast(new ProtobufDecoder(SqlBusEventMsgReq.getDefaultInstance()));

                    p.addLast(new ProtobufVarint32LengthFieldPrepender());
                    p.addLast(new ProtobufEncoder());

                    p.addLast(new SqlBusServerHandler(msgHandler));
                }
            });

            Channel channel = b.bind(sqlBusNettyConfig.getPort()).sync().channel();
            channel.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
