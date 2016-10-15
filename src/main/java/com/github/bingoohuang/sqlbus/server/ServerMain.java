package com.github.bingoohuang.sqlbus.server;

import com.github.bingoohuang.sqlbus.netty.SqlBusNettyConfig;
import com.github.bingoohuang.sqlbus.netty.SqlBusServer;
import lombok.val;

/**
 * @author bingoohuang [bingoohuang@gmail.com] Created on 2016/10/15.
 */
public class ServerMain {
    public static void main(String[] args) {
        val config = new SqlBusNettyConfig();
        val sqlBusServer = new SqlBusServer(config);

        val msHandler = new PrintMsgHandler();
        sqlBusServer.startup(msHandler);
    }
}
