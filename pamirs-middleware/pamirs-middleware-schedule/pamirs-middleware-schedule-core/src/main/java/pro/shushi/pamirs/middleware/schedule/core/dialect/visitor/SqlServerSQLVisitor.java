package pro.shushi.pamirs.middleware.schedule.core.dialect.visitor;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitorAdapter;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;

import java.util.function.BiConsumer;

public class SqlServerSQLVisitor extends MySqlASTVisitorAdapter {

    private static final String SQLSERVER_QUOTE = "\"";

    private static final String MYSQL_QUOTE = "`";

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
    public boolean visit(SQLMethodInvokeExpr x) {
        String methodName = x.getMethodName();
        if (SQLBinaryOperator.Mod.getName().equalsIgnoreCase(methodName)) {
            ValidSetter<?, SQLBinaryOpExpr> validSetter = getValidModParent(x);
            SQLExpr left = x.getArguments().get(0);
            SQLExpr right = x.getArguments().get(1);
            SQLBinaryOpExpr sqlBinaryOpExpr = new SQLBinaryOpExpr();
            sqlBinaryOpExpr.setLeft(left);
            sqlBinaryOpExpr.setRight(right);
            sqlBinaryOpExpr.setOperator(SQLBinaryOperator.Modulus);
            validSetter.accept(sqlBinaryOpExpr);
            return false;
        }
        return true;
    }

    private ValidSetter<?, SQLBinaryOpExpr> getValidModParent(SQLMethodInvokeExpr x) {
        SQLObject parentObject = x.getParent();
        ValidSetter<?, SQLBinaryOpExpr> validParent = null;
        if (parentObject instanceof SQLBinaryOpExpr) {
            SQLBinaryOpExpr binaryOpExpr = (SQLBinaryOpExpr) parentObject;
            if (x.equals(binaryOpExpr.getLeft())) {
                validParent = new ValidSetter<>(binaryOpExpr, SQLBinaryOpExpr::setLeft);
            } else if (x.equals(binaryOpExpr.getRight())) {
                validParent = new ValidSetter<>(binaryOpExpr, SQLBinaryOpExpr::setRight);
            }
        } else if (parentObject instanceof SQLInListExpr) {
            validParent = new ValidSetter<>((SQLInListExpr) parentObject, SQLInListExpr::setExpr);
        }
        return validParent;
    }

    protected String quote(String column) {
        if (column.startsWith(SQLSERVER_QUOTE) && column.endsWith(SQLSERVER_QUOTE)) {
            return null;
        }
        if (column.startsWith(MYSQL_QUOTE) && column.endsWith(MYSQL_QUOTE)) {
            return SQLSERVER_QUOTE + column.substring(1, column.length() - 1) + SQLSERVER_QUOTE;
        }
        return SQLSERVER_QUOTE + column + SQLSERVER_QUOTE;
    }

    private static class ValidSetter<T extends SQLObject, U extends SQLObject> {

        private final T target;

        private final BiConsumer<T, U> setter;

        public ValidSetter(T target, BiConsumer<T, U> setter) {
            this.target = target;
            this.setter = setter;
        }

        public void accept(U value) {
            this.setter.accept(this.target, value);
        }
    }
}