package com.github.bingoohuang.sqlbus;

import com.github.bingoohuang.sqlbus.impl.SqlAnatomy;
import com.github.bingoohuang.sqlbus.impl.SqlBusEvent;
import com.github.bingoohuang.sqlbus.impl.SqlCaringParser;
import com.github.bingoohuang.sqlbus.netty.SqlBusClient;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.eventbus.EventBus;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;

/**
 * @author bingoohuang [bingoohuang@gmail.com] Created on 2016/10/14.
 */
public class SqlBusConfig implements Closeable {
    private final EventBus eventBus;
    private final Map<String, SqlType[]> carings;
    private final SqlBusClient sqlBusClient;

    public SqlBusConfig(
            EventBus eventBus,
            Map<String, SqlType[]> carings,
            SqlBusClient sqlBusClient) {
        this.eventBus = eventBus;
        this.carings = carings;
        this.sqlBusClient = sqlBusClient;
    }

    public SqlType[] getCarings(String table) {
        return carings.get(table.toUpperCase());
    }

    LoadingCache<String, SqlAnatomy> sqlCache = CacheBuilder.newBuilder()
            .build(new SqlCaringCacheLoader());

    public boolean isCaredSql(String sql) {
        return sqlCache.getUnchecked(sql).isCaredSql();
    }

    public SqlAnatomy getSqlAnatomy(String sql) {
        return sqlCache.getUnchecked(sql);
    }

    public void post(SqlBusEvent sqlBusEvent) {
        eventBus.post(sqlBusEvent);
    }

    @Override public void close() throws IOException {
        if (sqlBusClient != null) sqlBusClient.close();
    }

    class SqlCaringCacheLoader extends CacheLoader<String, SqlAnatomy> {
        @Override
        public SqlAnatomy load(String sql) throws Exception {
            return new SqlCaringParser(SqlBusConfig.this, sql).parse();
        }
    }
}
