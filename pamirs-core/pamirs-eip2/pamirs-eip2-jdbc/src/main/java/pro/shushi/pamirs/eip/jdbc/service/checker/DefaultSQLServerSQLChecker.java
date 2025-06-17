package pro.shushi.pamirs.eip.jdbc.service.checker;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.util.JdbcUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.eip.jdbc.check.SQLServerSQLCheckVisitor;
import pro.shushi.pamirs.eip.jdbc.entity.SQLPrepareEntity;
import pro.shushi.pamirs.eip.jdbc.service.EipSQLChecker;

import java.util.List;

/**
 * SQLServer SQL检查器
 *
 * @author Adamancy Zhang at 17:29 on 2024-06-06
 */
@Component
public class DefaultSQLServerSQLChecker extends AbstractSQLChecker implements EipSQLChecker {

    @Override
    public String dbType() {
        return JdbcUtils.SQL_SERVER;
    }

    @Override
    public SQLASTVisitor visitor(SQLPrepareEntity prepareEntity) {
        return new SQLServerSQLCheckVisitor(prepareEntity.getPrepareParameters());
    }

    @Override
    public List<SQLStatement> parser(String sql) {
        return parseStatements(sql);
    }
}
