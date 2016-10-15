package com.github.bingoohuang.sqlbus;

import com.github.bingoohuang.sqlbus.impl.SqlBusRemoteWorker;
import com.github.bingoohuang.sqlbus.netty.SqlBusClient;
import com.github.bingoohuang.sqlbus.netty.SqlBusNettyConfig;
import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import lombok.val;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author bingoohuang [bingoohuang@gmail.com] Created on 2016/10/14.
 */
public class SqlBusConfigBuilder {
    public static SqlBusConfigBuilder newBuilder() {
        return new SqlBusConfigBuilder();
    }

    Map<String, SqlType[]> carings = new HashMap<String, SqlType[]>();

    public SqlBusConfigBuilder care(String table, SqlType... sqlTypes) {
        carings.put(table, sqlTypes);
        return this;
    }

    EventBus eventBus;

    public SqlBusConfigBuilder eventbus(EventBus eventBus) {
        if (!workers.isEmpty()) {
            throw new UnsupportedOperationException(
                    "eventbus can not be customized when customized workers are registered!");
        }

        this.eventBus = eventBus;
        return this;
    }

    List<Object> workers = Lists.newArrayList();

    public SqlBusConfigBuilder register(Object worker) {
        if (eventBus != null) {
            throw new UnsupportedOperationException(
                    "worker is not allowed to be registered when eventbus is customized!");
        }

        workers.add(worker);
        return this;
    }

    public SqlBusConfig build() {
        EventBus configEventBus = makeEventBus();
        val configCarings = new HashMap<String, SqlType[]>(carings);

        return new SqlBusConfig(configEventBus, configCarings, sqlBusClient);
    }

    SqlBusClient sqlBusClient;

    private EventBus makeEventBus() {
        if (eventBus != null) return eventBus;

        EventBus configEventBus = new EventBus();

        if (workers.isEmpty()) {
            val config = new SqlBusNettyConfig();
            sqlBusClient = new SqlBusClient(config);
            sqlBusClient.connect();

            val worker = new SqlBusRemoteWorker(sqlBusClient);

            workers.add(worker);
        }

        for (Object worker : workers) configEventBus.register(worker);

        return configEventBus;
    }
}
