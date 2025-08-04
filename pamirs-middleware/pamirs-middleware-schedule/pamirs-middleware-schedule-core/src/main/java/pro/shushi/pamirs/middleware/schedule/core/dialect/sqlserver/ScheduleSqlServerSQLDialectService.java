package pro.shushi.pamirs.middleware.schedule.core.dialect.sqlserver;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.util.JdbcUtils;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.middleware.schedule.core.dialect.AbstractSQLDialectService;
import pro.shushi.pamirs.middleware.schedule.core.dialect.ScheduleDialectConstants;
import pro.shushi.pamirs.middleware.schedule.core.dialect.ScheduleSQLDialectService;
import pro.shushi.pamirs.middleware.schedule.core.dialect.entity.DialectVersion;
import pro.shushi.pamirs.middleware.schedule.core.dialect.visitor.SqlServerSQLVisitor;

import java.util.List;

/**
 * SQLServer脚本执行方言服务
 *
 * @author Adamancy Zhang at 21:40 on 2024-10-18
 */
@Order
@Component
public class ScheduleSqlServerSQLDialectService extends AbstractSQLDialectService implements ScheduleSQLDialectService {

    @Override
    public boolean isSupported(DialectVersion dialectVersion, MappedStatement mappedStatement) {
        return ScheduleDialectConstants.PRODUCT_SQL_SERVER.equals(dialectVersion.getType());
    }

    @Override
    protected String getOriginType() {
        return JdbcUtils.MYSQL.name();
    }

    @Override
    protected DbType getTargetType() {
        return JdbcUtils.SQL_SERVER;
    }

    @Override
    protected SQLASTVisitor getSQLVisitor(List<ResultMap> resultMaps) {
        return new SqlServerSQLVisitor();
    }

    @Override
    public String resolve(String sql, BoundSql boundSql, List<ResultMap> resultMaps) {
        swapLimitOffset(boundSql);
        return super.resolve(sql, boundSql, resultMaps);
    }
}
