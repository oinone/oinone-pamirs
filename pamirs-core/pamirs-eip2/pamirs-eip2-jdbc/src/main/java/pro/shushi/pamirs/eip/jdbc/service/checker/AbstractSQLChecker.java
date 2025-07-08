package pro.shushi.pamirs.eip.jdbc.service.checker;

import com.alibaba.druid.sql.ast.SQLStatement;
import pro.shushi.pamirs.eip.jdbc.entity.SQLPrepareEntity;
import pro.shushi.pamirs.eip.jdbc.helper.SQLPrepareHelper;
import pro.shushi.pamirs.eip.jdbc.service.EipSQLChecker;

import java.util.List;

/**
 * 抽象SQL检查器
 *
 * @author Adamancy Zhang at 17:33 on 2024-06-06
 */
public abstract class AbstractSQLChecker implements EipSQLChecker {

    @Override
    public SQLPrepareEntity prepare(String sql) {
        return SQLPrepareHelper.prepareParameters(sql);
    }

    protected List<SQLStatement> parseStatements(String sql) {
        return parseStatements(sql, dbType());
    }

    protected List<SQLStatement> parseStatements(String sql, String dbType) {
        return SQLPrepareHelper.parseStatements(sql, dbType);
    }
}
