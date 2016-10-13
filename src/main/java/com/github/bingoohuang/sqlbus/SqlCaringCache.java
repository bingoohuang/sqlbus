package com.github.bingoohuang.sqlbus;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.experimental.UtilityClass;

/**
 * @author bingoohuang [bingoohuang@gmail.com] Created on 2016/10/13.
 */
@UtilityClass
public class SqlCaringCache {
    LoadingCache<String, SqlAnatomy> cache = CacheBuilder.newBuilder()
            .build(new CacheLoader<String, SqlAnatomy>() {
                @Override public SqlAnatomy load(String sql) throws Exception {
                    return new SqlCaringParser(sql).parse();
                }
            });

    public boolean isCaredSql(String sql) {
        return cache.getUnchecked(sql).isCaredSql();
    }

    public SqlAnatomy getSqlAnatomy(String sql) {
        return cache.getUnchecked(sql);
    }
}
