package com.github.bingoohuang.sqlbus;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleInsertStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleUpdateStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import lombok.val;

import java.util.ArrayList;
import java.util.List;

/**
 * @author bingoohuang [bingoohuang@gmail.com] Created on 2016/10/13.
 */
public class SqlCaringParser {
    private final SqlBusConfig sqlBusConfig;
    private final String rawSql;

    public SqlCaringParser(SqlBusConfig sqlBusConfig, String rawSql) {
        this.sqlBusConfig = sqlBusConfig;
        this.rawSql = rawSql;
    }

    public SqlAnatomy parse() {
        val stmtParser = new OracleStatementParser(rawSql);
        val stmt = stmtParser.parseStatement();

        SqlAnatomy sqlAnatomy = null;
        if (stmt instanceof OracleInsertStatement) {
            sqlAnatomy = parseInsert((OracleInsertStatement) stmt);
        } else if (stmt instanceof OracleUpdateStatement) {
            sqlAnatomy = parseUpdate((OracleUpdateStatement) stmt);
        }

        if (sqlAnatomy != null) return sqlAnatomy;
        return new SqlAnatomy(RawSqlType.NA, rawSql, "NA");
    }

    private SqlAnatomy parseUpdate(OracleUpdateStatement stmt) {
        val table = stmt.getTableName().getSimpleName();
        if (!contains(table, RawSqlType.UPDATE)) return null;

        return new SqlAnatomy(RawSqlType.UPDATE, rawSql, table);
    }

    private boolean contains(String table, RawSqlType rawSqlType) {
        RawSqlType[] carings = sqlBusConfig.getCarings(table);
        if (carings == null) return false;

        for (RawSqlType caring : carings)
            if (caring == rawSqlType) return true;

        return false;
    }

    private SqlAnatomy parseInsert(OracleInsertStatement stmt) {
        val table = stmt.getTableName().getSimpleName();
        if (!contains(table, RawSqlType.INSERT)) return null;

        return new SqlAnatomy(RawSqlType.INSERT, rawSql, table);
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
