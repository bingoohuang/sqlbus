package com.github.bingoohuang.sqlbus;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleInsertStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import lombok.val;

import java.util.ArrayList;
import java.util.List;

/**
 * @author bingoohuang [bingoohuang@gmail.com] Created on 2016/10/13.
 */
public class SqlCaringParser {
    private final String rawSql;

    public SqlCaringParser(String sql) {
        this.rawSql = sql;
    }

    public SqlAnatomy parse() {
        val orclParser = new OracleStatementParser(rawSql);
        val stmt = orclParser.parseStatement();
        if (stmt instanceof OracleInsertStatement) {
            return parseInsert((OracleInsertStatement)stmt);
        }

        return null;
    }

    private SqlAnatomy parseInsert(OracleInsertStatement stmt) {
        val tableName = stmt.getTableName().getSimpleName();
        val columns = parseColumns(stmt.getColumns());

        return new SqlAnatomy(tableName, columns);
    }

    private List<String> parseColumns(List<SQLExpr> columns) {
        val columnNames = new ArrayList<String>(columns.size());
        for (SQLExpr sqlExpr: columns) {
            if (sqlExpr instanceof SQLIdentifierExpr) {
                val sqlIdentifierExpr = (SQLIdentifierExpr)sqlExpr;
                columnNames.add(sqlIdentifierExpr.getName());
            }
        }

        return columnNames;
    }
}
