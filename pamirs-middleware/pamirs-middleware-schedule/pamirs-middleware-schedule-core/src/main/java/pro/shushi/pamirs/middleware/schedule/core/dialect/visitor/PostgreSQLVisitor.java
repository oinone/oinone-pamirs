package pro.shushi.pamirs.middleware.schedule.core.dialect.visitor;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBooleanExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLUpdateSetItem;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitorAdapter;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * PostgreSQL SQL Visitor
 *
 * @author Adamancy Zhang at 16:52 on 2023-08-03
 */
public class PostgreSQLVisitor extends MySqlASTVisitorAdapter {

    private static final String PG_QUOTE = "\"";

    private static final String MYSQL_QUOTE = "`";

    private static final String BOOLEAN_FALSE_NUMBER = "0";

    private static final String BOOLEAN_TRUE_NUMBER = "1";

    private final List<ResultMap> resultMaps;

    public PostgreSQLVisitor(List<ResultMap> resultMaps) {
        this.resultMaps = Optional.ofNullable(resultMaps).orElse(new ArrayList<>());
    }

    @Override
    public boolean visit(SQLIdentifierExpr x) {
        String column = x.getName();
        String formatColumn = quote(column);
        if (formatColumn == null) {
            return true;
        }
        x.setName(formatColumn);
        return true;
    }

    @Override
    public boolean visit(SQLSelectItem x) {
        String alias = x.getAlias();
        if (StringUtils.isBlank(alias)) {
            return true;
        }
        String formatAlias = quote(alias);
        if (formatAlias == null) {
            return true;
        }
        x.setAlias(formatAlias);
        return true;
    }

    @Override
    public boolean visit(SQLUpdateSetItem x) {
        SQLExpr left = x.getColumn();
        SQLExpr right = x.getValue();
        if (left instanceof SQLIdentifierExpr && right instanceof SQLIntegerExpr) {
            return visitUpdateSetItem(x, (SQLIdentifierExpr) left, (SQLIntegerExpr) right);
        }
        return true;
    }

    private boolean visitUpdateSetItem(SQLUpdateSetItem x, SQLIdentifierExpr left, SQLIntegerExpr right) {
        String column = clearQuote(left.getName());
        ResultMapping resultMapping = findResultMapping(column);
        if (resultMapping == null) {
            return true;
        }
        if (Boolean.class.equals(resultMapping.getJavaType())) {
            String value = right.getNumber().toString();
            boolean isVisitChildren = true;
            if (BOOLEAN_FALSE_NUMBER.equals(value)) {
                x.setValue(new SQLBooleanExpr(false));
                isVisitChildren = false;
            } else if (BOOLEAN_TRUE_NUMBER.equals(value)) {
                x.setValue(new SQLBooleanExpr(true));
                isVisitChildren = false;
            }
            if (!isVisitChildren) {
                left.setName(quote0(column));
            }
            return isVisitChildren;
        }
        return true;
    }

    @Override
    public boolean visit(SQLBinaryOpExpr x) {
        SQLExpr left = x.getLeft();
        SQLExpr right = x.getRight();
        if (left instanceof SQLIdentifierExpr && right instanceof SQLIntegerExpr) {
            return visitBinaryOpExpr(x, (SQLIdentifierExpr) left, (SQLIntegerExpr) right);
        }
        return true;
    }

    private boolean visitBinaryOpExpr(SQLBinaryOpExpr x, SQLIdentifierExpr left, SQLIntegerExpr right) {
        String column = clearQuote(left.getName());
        ResultMapping resultMapping = findResultMapping(column);
        if (resultMapping == null) {
            return true;
        }
        if (Boolean.class.equals(resultMapping.getJavaType())) {
            String value = right.getNumber().toString();
            boolean isVisitChildren = true;
            if (BOOLEAN_FALSE_NUMBER.equals(value)) {
                x.setRight(new SQLBooleanExpr(false));
                isVisitChildren = false;
            } else if (BOOLEAN_TRUE_NUMBER.equals(value)) {
                x.setRight(new SQLBooleanExpr(true));
                isVisitChildren = false;
            }
            if (!isVisitChildren) {
                left.setName(quote0(column));
            }
            return isVisitChildren;
        }
        return true;
    }

    protected String quote(String column) {
        if (column.startsWith(PG_QUOTE) && column.endsWith(PG_QUOTE)) {
            return null;
        }
        if (column.startsWith(MYSQL_QUOTE) && column.endsWith(MYSQL_QUOTE)) {
            return PG_QUOTE + column.substring(1, column.length() - 1) + PG_QUOTE;
        }
        return quote0(column);
    }

    protected String quote0(String column) {
        return PG_QUOTE + column + PG_QUOTE;
    }

    protected String clearQuote(String column) {
        if (column.startsWith(PG_QUOTE) && column.endsWith(PG_QUOTE)) {
            return column.substring(1, column.length() - 1);
        }
        if (column.startsWith(MYSQL_QUOTE) && column.endsWith(MYSQL_QUOTE)) {
            return column.substring(1, column.length() - 1);
        }
        return column;
    }

    protected ResultMapping findResultMapping(String column) {
        for (ResultMap resultMap : this.resultMaps) {
            List<ResultMapping> resultMappings = resultMap.getResultMappings();
            for (ResultMapping resultMapping : resultMappings) {
                if (column.equals(resultMapping.getColumn())) {
                    return resultMapping;
                }
            }
        }
        return null;
    }
}
