package pro.shushi.pamirs.middleware.schedule.core.dialect.visitor;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLLimit;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;

/**
 * @author Gesi at 14:24 on 2025/7/15
 */
public class Oracle11gSQLVisitor extends OracleSQLVisitor {

    @Override
    public boolean visit(SQLSelectStatement selectStatement) {
        SQLSelect select = selectStatement.getSelect();
        SQLSelectQuery query = select.getQuery();
        if (query instanceof SQLSelectQueryBlock) {
            SQLSelectQueryBlock queryBlock = (SQLSelectQueryBlock) query;
            SQLLimit limit = queryBlock.getLimit();
            if (limit != null && limit.getRowCount() != null) {
                // 创建嵌套结构
                SQLSelect newSelect = buildOracle11gPagination(queryBlock, limit);
                selectStatement.setSelect(newSelect);
                return true;
            }
        }
        return true;
    }

    private SQLSelect buildOracle11gPagination(SQLSelectQueryBlock originalBlock, SQLLimit limit) {
        SQLExpr offset = limit.getOffset();
        SQLExpr rowCount = limit.getRowCount();

        // clone inner select block（不带limit）
        SQLSelectQueryBlock innerQuery = originalBlock;
        innerQuery.setLimit(null);

        // SELECT INNER_TABLE.*, ROWNUM AS ROWNUM_
        SQLSelectQueryBlock midQuery = new SQLSelectQueryBlock();
        midQuery.setFrom(new SQLSubqueryTableSource(new SQLSelect(innerQuery), "INNER_TABLE"));

        SQLSelectItem selectAll = new SQLSelectItem(new SQLPropertyExpr(new SQLIdentifierExpr("INNER_TABLE"), "*"));
        SQLSelectItem rownumItem = new SQLSelectItem(new SQLIdentifierExpr("ROWNUM"), "ROWNUM_");
        midQuery.getSelectList().add(selectAll);
        midQuery.getSelectList().add(rownumItem);

        // WHERE ROWNUM <= offset + rowCount
        if (rowCount != null) {
            SQLExpr rownumWhereExpr;
            if (offset != null) {
                rownumWhereExpr = new SQLBinaryOpExpr(
                        new SQLIdentifierExpr("ROWNUM"),
                        SQLBinaryOperator.LessThanOrEqual,
                        new SQLBinaryOpExpr(offset, SQLBinaryOperator.Add, rowCount)
                );
            } else {
                rownumWhereExpr = new SQLBinaryOpExpr(
                        new SQLIdentifierExpr("ROWNUM"),
                        SQLBinaryOperator.LessThanOrEqual,
                        rowCount
                );
            }
            midQuery.setWhere(rownumWhereExpr);
        }

        // 最外层查询：WHERE ROWNUM_ > offset
        SQLSelectQueryBlock outerQuery = new SQLSelectQueryBlock();
        outerQuery.setFrom(new SQLSubqueryTableSource(new SQLSelect(midQuery), "OUTER_TABLE"));
        outerQuery.getSelectList().add(new SQLSelectItem(new SQLIdentifierExpr("*")));

        if (offset != null) {
            SQLBinaryOpExpr outerWhere = new SQLBinaryOpExpr(
                    new SQLIdentifierExpr("ROWNUM_"),
                    SQLBinaryOperator.GreaterThan,
                    offset
            );
            outerQuery.setWhere(outerWhere);
        }

        return new SQLSelect(outerQuery);
    }

    @Override
    public boolean visit(SQLMethodInvokeExpr x) {
        for (int i = 0; i < x.getChildren().size(); i++) {
            if (x.getChildren().get(i) instanceof SQLBinaryOpExpr) {
                SQLBinaryOpExpr expr = (SQLBinaryOpExpr) x.getChildren().get(i);
                if (SQLBinaryOperator.Modulus.equals(expr.getOperator())) {
                    SQLExpr left = expr.getLeft();
                    SQLExpr right = expr.getRight();
                    SQLMethodInvokeExpr mod = new SQLMethodInvokeExpr("MOD");
                    mod.addArgument(left);
                    mod.addArgument(right);
                    x.getChildren().set(i, mod);
                }
            }
        }
        return true;
    }

    @Override
    public boolean visit(MySqlInsertStatement x) {
        String tableName = x.getTableName().getSimpleName().toUpperCase();
        if (tableName.startsWith("PAMIRS_SCHEDULE")) {
            String sequenceName = x.getTableName().getSimpleName().toUpperCase() + "_ID";
            x.getColumns().add(new SQLIdentifierExpr("id"));
            x.getValues().addValue(new SQLSequenceExpr(new SQLIdentifierExpr(sequenceName), SQLSequenceExpr.Function.NextVal));
        }
        return true;
    }
}
