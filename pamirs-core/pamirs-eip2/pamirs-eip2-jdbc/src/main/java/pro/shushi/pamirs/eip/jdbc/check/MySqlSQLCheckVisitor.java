package pro.shushi.pamirs.eip.jdbc.check;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlDeleteStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlUpdateStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitorAdapter;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.eip.jdbc.check.exception.SQLCommonCheckException;
import pro.shushi.pamirs.eip.jdbc.check.exception.SQLDeleteCheckException;
import pro.shushi.pamirs.eip.jdbc.check.exception.SQLSelectCheckException;
import pro.shushi.pamirs.eip.jdbc.check.exception.SQLUpdateCheckException;
import pro.shushi.pamirs.eip.jdbc.helper.SQLCheckHelper;
import pro.shushi.pamirs.eip.jdbc.helper.SQLPrepareHelper;

import java.util.Map;

/**
 * MySql sql checker
 *
 * @author Adamancy Zhang at 15:20 on 2024-05-17
 */
public class MySqlSQLCheckVisitor extends MySqlASTVisitorAdapter {

    private static final String MYSQL_QUOTE = "`";

    private final Map<Integer, String> prepareParameters;

    public MySqlSQLCheckVisitor(Map<Integer, String> prepareParameters) {
        this.prepareParameters = prepareParameters;
    }

    @Override
    public boolean visit(MySqlSelectQueryBlock x) {
        if (x.getWhere() == null) {
            throw SQLSelectCheckException.createWhereIsNullException();
        }
        return true;
    }

    @Override
    public boolean visit(MySqlUpdateStatement x) {
        if (x.getWhere() == null) {
            throw SQLUpdateCheckException.createWhereIsNullException();
        }
        return true;
    }

    @Override
    public boolean visit(MySqlDeleteStatement x) {
        if (x.getWhere() == null) {
            throw SQLDeleteCheckException.createWhereIsNullException();
        }
        return true;
    }

    @Override
    public boolean visit(SQLExprTableSource x) {
        SQLExpr tableExpr = x.getExpr();
        String tableName;
        if (tableExpr instanceof SQLIdentifierExpr) {
            SQLIdentifierExpr tableIdentifierExpr = (SQLIdentifierExpr) tableExpr;
            tableName = tableIdentifierExpr.getName();
        } else if (tableExpr instanceof SQLPropertyExpr) {
            SQLPropertyExpr tablePropertyExpr = (SQLPropertyExpr) tableExpr;
            tableName = tablePropertyExpr.getName();
        } else {
            throw SQLCommonCheckException.createTableNameIsNullException();
        }
        tableName = SQLCheckHelper.clearQuote(tableName, MYSQL_QUOTE);
        if (SQLCheckHelper.isLimitTable(tableName)) {
            throw SQLCommonCheckException.createNotAllowOperationTableException(tableName);
        }
        return true;
    }

    @Override
    public boolean visit(SQLCharExpr x) {
        String text = x.getText();
        if (StringUtils.isBlank(text)) {
            return true;
        }
        x.setText(SQLPrepareHelper.replacePrepareParameters(prepareParameters, text));
        return true;
    }

    @Override
    public boolean visit(SQLVariantRefExpr x) {
        if (processPrepareParameter(x)) {
            return false;
        }
        return true;
    }

    private boolean processPrepareParameter(SQLVariantRefExpr expr) {
        String name = expr.getName();
        Integer key = SQLPrepareHelper.parsePrepareParameterKey(name);
        if (key != null) {
            String targetName = prepareParameters.get(key);
            if (targetName != null) {
                expr.setName(targetName);
                return true;
            }
        }
        return false;
    }
}
