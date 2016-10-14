package com.github.bingoohuang.sqlbus;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.eventbus.EventBus;

import java.util.Map;

/**
 * @author bingoohuang [bingoohuang@gmail.com] Created on 2016/10/14.
 */
public class SqlBusConfig {
    private final EventBus eventBus;
    private final Map<String, RawSqlType[]> carings;

    public SqlBusConfig(EventBus eventBus, Map<String, RawSqlType[]> carings) {
        this.eventBus = eventBus;
        this.carings = carings;
    }

    public RawSqlType[] getCarings(String table) {
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

    class SqlCaringCacheLoader extends CacheLoader<String, SqlAnatomy> {
        @Override
        public SqlAnatomy load(String sql) throws Exception {
            return new SqlCaringParser(SqlBusConfig.this, sql).parse();
        }
    }
}
