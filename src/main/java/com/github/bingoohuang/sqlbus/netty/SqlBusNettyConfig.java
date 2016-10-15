package com.github.bingoohuang.sqlbus.netty;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.val;

/**
 * @author bingoohuang [bingoohuang@gmail.com] Created on 2016/10/15.
 */
@Setter @Getter
public class SqlBusNettyConfig {
    private String host = System.getProperty("host", "127.0.0.1");
    private int port = Integer.parseInt(System.getProperty("port", "8366"));
    // Sleep 5 seconds before a reconnection attempt.
    private int reconnectDelay = Integer.parseInt(System.getProperty("reconnectDelay", "5"));
    // Reconnect when the server sends nothing for 10 seconds.
    private int readTimeout = Integer.parseInt(System.getProperty("readTimeout", "10"));

    @SneakyThrows
    public SslContext configureSslForServer() {
        boolean ssl = System.getProperty("ssl") != null;
        if (!ssl) return null;

        val ssc = new SelfSignedCertificate();
        return SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
    }

    @SneakyThrows
    public SslContext configureSslForClient() {
        boolean ssl = System.getProperty("ssl") != null;
        if (!ssl) return null;

        val instance = InsecureTrustManagerFactory.INSTANCE;
        return SslContextBuilder.forClient().trustManager(instance).build();
    }

    public String getHostPort() {
        return host + ":" + port;
    }
}
