package pro.shushi.pamirs.middleware.schedule.core.dialect.gauss;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.util.JdbcUtils;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.middleware.schedule.core.dialect.AbstractSQLDialectService;
import pro.shushi.pamirs.middleware.schedule.core.dialect.ScheduleDialectConstants;
import pro.shushi.pamirs.middleware.schedule.core.dialect.ScheduleSQLDialectService;
import pro.shushi.pamirs.middleware.schedule.core.dialect.entity.DialectVersion;
import pro.shushi.pamirs.middleware.schedule.core.dialect.visitor.PostgreSQLVisitor;

import java.util.List;

/**
 * GaussDB 脚本执行方言服务
 *
 * @author wangxian at 2024-03-29
 */
@Order
@Component
public class ScheduleGaussSQLDialectService extends AbstractSQLDialectService implements ScheduleSQLDialectService {

    @Override
    public boolean isSupported(DialectVersion dialectVersion, MappedStatement mappedStatement) {
        return ScheduleDialectConstants.PRODUCT_GAUSS_DB.equals(dialectVersion.getType());
    }

    @Override
    protected String getOriginType() {
        return JdbcUtils.MYSQL.name();
    }

    @Override
    protected DbType getTargetType() {
        return JdbcUtils.POSTGRESQL;
    }

    @Override
    protected SQLASTVisitor getSQLVisitor(List<ResultMap> resultMaps) {
        return new PostgreSQLVisitor(resultMaps);
    }
}
