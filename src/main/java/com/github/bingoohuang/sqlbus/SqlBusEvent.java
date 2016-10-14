package com.github.bingoohuang.sqlbus;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @author bingoohuang [bingoohuang@gmail.com] Created on 2016/10/14.
 */
@Data @AllArgsConstructor
public class SqlBusEvent {
    String table;
    RawSqlType sqlType;
    String sql;
    List<Object> parameters;
}
