package pro.shushi.pamirs.eip.jdbc.service.checker;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.util.JdbcUtils;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.eip.jdbc.check.MySqlSQLCheckVisitor;
import pro.shushi.pamirs.eip.jdbc.check.OracleSQLCheckVisitor;
import pro.shushi.pamirs.eip.jdbc.check.SQLServerSQLCheckVisitor;
import pro.shushi.pamirs.eip.jdbc.entity.SQLPrepareEntity;
import pro.shushi.pamirs.eip.jdbc.service.EipSQLChecker;

import java.util.List;

/**
 * DefaultKingbaseSQLChecker
 *
 * @author yakir on 2025/06/16 17:40.
 */
@Component
public class DefaultKingbaseSQLChecker extends AbstractSQLChecker implements EipSQLChecker {

    @Override
    public String dbType() {
        return JdbcUtils.KINGBASE.name();
    }

    @Override
    public List<String> secondaryDbTypes() {
        return Lists.newArrayList(
                JdbcUtils.ORACLE.name(),
                JdbcUtils.SQL_SERVER.name(),
                JdbcUtils.MYSQL.name()
        );
    }

    @Override
    public SQLASTVisitor visitor(SQLPrepareEntity prepareEntity) {
        SQLASTVisitor visitor = null;
        ParserException exception = null;
        try {
            visitor = new MySqlSQLCheckVisitor(prepareEntity.getPrepareParameters());
        } catch (ParserException e) {
            exception = e;
        }

        if (null != visitor) {
            return visitor;
        }

        try {
            visitor = new OracleSQLCheckVisitor(prepareEntity.getPrepareParameters());
        } catch (ParserException e) {
            exception = e;
        }

        if (null != visitor) {
            return visitor;
        }

        try {
            visitor = new SQLServerSQLCheckVisitor(prepareEntity.getPrepareParameters());
        } catch (ParserException e) {
            exception = e;
        }

        if (null != visitor) {
            return visitor;
        }

        throw exception;
    }

    @Override
    public List<SQLStatement> parser(String sql) {
        return parseStatements(sql);
    }
}
