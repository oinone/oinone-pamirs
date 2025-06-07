package pro.shushi.pamirs.eip.jdbc.check;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleDeleteStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectQueryBlock;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectTableReference;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleUpdateStatement;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitorAdapter;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.eip.jdbc.check.exception.SQLCommonCheckException;
import pro.shushi.pamirs.eip.jdbc.check.exception.SQLDeleteCheckException;
import pro.shushi.pamirs.eip.jdbc.check.exception.SQLSelectCheckException;
import pro.shushi.pamirs.eip.jdbc.check.exception.SQLUpdateCheckException;
import pro.shushi.pamirs.eip.jdbc.helper.SQLCheckHelper;
import pro.shushi.pamirs.eip.jdbc.helper.SQLPrepareHelper;

import java.util.Map;

/**
 * Oracle sql checker
 *
 * @author Adamancy Zhang at 15:20 on 2024-05-17
 */
public class OracleSQLCheckVisitor extends OracleASTVisitorAdapter {

    private static final String ORACLE_QUOTE = "\"";

    private final Map<Integer, String> prepareParameters;

    public OracleSQLCheckVisitor(Map<Integer, String> prepareParameters) {
        this.prepareParameters = prepareParameters;
    }

    @Override
    public boolean visit(OracleSelectQueryBlock x) {
        if (x.getWhere() == null) {
            throw SQLSelectCheckException.createWhereIsNullException();
        }
        return true;
    }

    @Override
    public boolean visit(OracleUpdateStatement x) {
        if (x.getWhere() == null) {
            throw SQLUpdateCheckException.createWhereIsNullException();
        }
        return true;
    }

    @Override
    public boolean visit(OracleDeleteStatement x) {
        if (x.getWhere() == null) {
            throw SQLDeleteCheckException.createWhereIsNullException();
        }
        return true;
    }

    @Override
    public boolean visit(OracleSelectTableReference x) {
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
        tableName = SQLCheckHelper.clearQuote(tableName, ORACLE_QUOTE);
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
