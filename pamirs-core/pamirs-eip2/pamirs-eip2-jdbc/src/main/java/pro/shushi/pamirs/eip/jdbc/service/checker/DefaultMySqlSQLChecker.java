package pro.shushi.pamirs.eip.jdbc.service.checker;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.util.JdbcUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.eip.jdbc.check.MySqlSQLCheckVisitor;
import pro.shushi.pamirs.eip.jdbc.entity.SQLPrepareEntity;
import pro.shushi.pamirs.eip.jdbc.service.EipSQLChecker;

import java.util.List;

/**
 * MySql SQL检查器
 *
 * @author Adamancy Zhang at 17:29 on 2024-06-06
 */
@Component
public class DefaultMySqlSQLChecker extends AbstractSQLChecker implements EipSQLChecker {

    @Override
    public String dbType() {
        return JdbcUtils.MYSQL;
    }

    @Override
    public SQLASTVisitor visitor(SQLPrepareEntity prepareEntity) {
        return new MySqlSQLCheckVisitor(prepareEntity.getPrepareParameters());
    }

    @Override
    public List<SQLStatement> parser(String sql) {
        return parseStatements(sql);
    }
}
