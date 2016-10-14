package com.github.bingoohuang.sqlbus;

import com.google.common.eventbus.Subscribe;

/**
 * @author bingoohuang [bingoohuang@gmail.com] Created on 2016/10/14.
 */
public class RemoteSqlBusWorker {
    @Subscribe
    public void sendToRemote(SqlBusEvent sqlBusEvent) {
        System.out.println(sqlBusEvent);
    }
}
