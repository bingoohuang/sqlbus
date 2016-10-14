package com.github.bingoohuang.sqlbus;

import com.google.common.collect.Lists;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import lombok.val;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * @author bingoohuang [bingoohuang@gmail.com] Created on 2016/10/14.
 */
public class SqlBusConfigBuilder {
    public static SqlBusConfigBuilder newBuilder() {
        return new SqlBusConfigBuilder();
    }

    Map<String, RawSqlType[]> carings = new HashMap<String, RawSqlType[]>();

    public SqlBusConfigBuilder care(String table, RawSqlType... rawSqlTypes) {
        carings.put(table, rawSqlTypes);
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
        val configCarings = new HashMap<String, RawSqlType[]>(carings);

        return new SqlBusConfig(configEventBus, configCarings);
    }

    private EventBus makeEventBus() {
        if (eventBus != null) return eventBus;

        val threadPool = Executors.newFixedThreadPool(1);
        EventBus configEventBus = new AsyncEventBus(threadPool);

        if (workers.isEmpty()) workers.add(new RemoteSqlBusWorker());
        for (Object worker : workers) configEventBus.register(worker);

        return configEventBus;
    }
}
