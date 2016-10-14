package com.github.bingoohuang.sqlbus;

import lombok.Getter;

import java.util.List;

/**
 * @author bingoohuang [bingoohuang@gmail.com] Created on 2016/10/14.
 */
public class UpdateSqlAnatomy {
    @Getter final SqlAnatomy sqlAnatomy;
    @Getter final List<String> parameters;

    public UpdateSqlAnatomy(SqlAnatomy sqlAnatomy, List<String> parameters) {
        this.sqlAnatomy = sqlAnatomy;
        this.parameters = parameters;
    }
}
