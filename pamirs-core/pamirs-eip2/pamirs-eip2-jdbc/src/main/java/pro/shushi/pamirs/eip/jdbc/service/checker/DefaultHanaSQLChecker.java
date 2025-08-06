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

/*
 * DefaultHanaSQLChecker
 * @author : Haibo(xf.z@shushi.pro)
 * @date : 2025/7/9 18:22
 */
@Component
public class DefaultHanaSQLChecker extends AbstractSQLChecker implements EipSQLChecker {

    @Override
    public String dbType() {
        return JdbcUtils.KINGBASE;
    }

    @Override
    public List<String> secondaryDbTypes() {
        return Lists.newArrayList(
                "HANA",
                JdbcUtils.SQL_SERVER,
                JdbcUtils.MYSQL
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
