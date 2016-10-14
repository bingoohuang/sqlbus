package com.github.bingoohuang.sqlbus;

import lombok.Getter;

/**
 * @author bingoohuang [bingoohuang@gmail.com] Created on 2016/10/13.
 */
public class SqlAnatomy {
    @Getter final RawSqlType rawSqlType;
    @Getter final String rawSql;
    @Getter final String table;

    public SqlAnatomy(RawSqlType rawSqlType, String rawSql, String tableName) {
        this.rawSqlType = rawSqlType;
        this.rawSql = rawSql;
        this.table = tableName;
    }

    public boolean isCaredSql() {
        return rawSqlType != RawSqlType.NA;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SqlAnatomy that = (SqlAnatomy) o;

        return rawSql != null ? rawSql.equals(that.rawSql) : that.rawSql == null;
    }

    @Override public int hashCode() {
        return rawSql != null ? rawSql.hashCode() : 0;
    }
}
