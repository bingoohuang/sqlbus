package com.github.bingoohuang.sqlbus;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.experimental.UtilityClass;

/**
 * @author bingoohuang [bingoohuang@gmail.com] Created on 2016/10/14.
 */
@UtilityClass
public class SqlBusConfig {
    Cache<String, RawSqlType[]> configCache = CacheBuilder.newBuilder().build();

    public void care(String table, RawSqlType... rawSqlTypes) {
        configCache.put(table, rawSqlTypes);
    }

    public void clear(String table) {
        configCache.invalidate(table);
    }

    public RawSqlType[] getCarings(String table) {
        return configCache.getIfPresent(table);
    }
}
