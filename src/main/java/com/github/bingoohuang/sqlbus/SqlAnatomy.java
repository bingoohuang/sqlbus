package com.github.bingoohuang.sqlbus;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * @author bingoohuang [bingoohuang@gmail.com] Created on 2016/10/13.
 */
public class SqlAnatomy {
    private final String table;
    private final List<String> columns;
    private List<String> parameters;

    public SqlAnatomy(String table, List<String> columns) {
        this.table = table;
        this.columns = columns;
    }

    @JSONField(serialize = false)
    public boolean isCaredSql() {
        return true;
    }

    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }

    public String getTable() {
        return table;
    }

    public List<String> getColumns() {
        return columns;
    }

    public List<String> getParameters() {
        return parameters;
    }
}
