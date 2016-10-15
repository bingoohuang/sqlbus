package com.github.bingoohuang.sqlbus.impl;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.github.bingoohuang.sqlbus.SqlType;
import com.github.bingoohuang.sqlbus.SqlBusConfig;
import lombok.val;

import java.util.ArrayList;
import java.util.List;

/**
 * @author bingoohuang [bingoohuang@gmail.com] Created on 2016/10/13.
 */
public class SqlCaringParser {
    private final SqlBusConfig sqlBusConfig;
    private final String rawSql;

    public SqlCaringParser(
            SqlBusConfig sqlBusConfig,
            String rawSql) {
        this.sqlBusConfig = sqlBusConfig;
        this.rawSql = rawSql;
    }

    public SqlAnatomy parse() {
        val stmtParser = new SQLStatementParser(rawSql);
        val stmt = stmtParser.parseStatement();

        SqlAnatomy sqlAnatomy = null;
        if (stmt instanceof SQLInsertStatement) {
            sqlAnatomy = parseInsert((SQLInsertStatement) stmt);
        } else if (stmt instanceof SQLUpdateStatement) {
            sqlAnatomy = parseUpdate((SQLUpdateStatement) stmt);
        }

        if (sqlAnatomy != null) return sqlAnatomy;
        return new SqlAnatomy(SqlType.NA, rawSql, "NA");
    }

    private SqlAnatomy parseUpdate(SQLUpdateStatement stmt) {
        val table = stmt.getTableName().getSimpleName();
        if (!contains(table, SqlType.UPDATE)) return null;

        return new SqlAnatomy(SqlType.UPDATE, rawSql, table);
    }

    private boolean contains(String table, SqlType sqlType) {
        SqlType[] carings = sqlBusConfig.getCarings(table);
        if (carings == null) return false;

        for (SqlType caring : carings)
            if (caring == sqlType) return true;

        return false;
    }

    private SqlAnatomy parseInsert(SQLInsertStatement stmt) {
        val table = stmt.getTableName().getSimpleName();
        if (!contains(table, SqlType.INSERT)) return null;

        return new SqlAnatomy(SqlType.INSERT, rawSql, table);
    }

    private List<String> parseInsertColumns(List<SQLExpr> columns) {
        val columnNames = new ArrayList<String>(columns.size());
        for (SQLExpr sqlExpr : columns) {
            if (sqlExpr instanceof SQLIdentifierExpr) {
                val sqlIdentifierExpr = (SQLIdentifierExpr) sqlExpr;
                columnNames.add(sqlIdentifierExpr.getName());
            }
        }

        return columnNames;
    }
}
