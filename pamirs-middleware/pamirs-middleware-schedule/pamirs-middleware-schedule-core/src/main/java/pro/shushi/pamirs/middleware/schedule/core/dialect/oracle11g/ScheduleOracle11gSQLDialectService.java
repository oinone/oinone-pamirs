package pro.shushi.pamirs.middleware.schedule.core.dialect.oracle11g;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.util.JdbcUtils;
import org.apache.ibatis.mapping.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.middleware.schedule.core.dialect.*;
import pro.shushi.pamirs.middleware.schedule.core.dialect.entity.DialectVersion;
import pro.shushi.pamirs.middleware.schedule.core.dialect.visitor.Oracle11gSQLVisitor;

import java.util.List;

/**
 * ORACLE脚本执行方言服务
 *
 * @author Adamancy Zhang at 21:50 on 2023-06-27
 */
@Order(55)
@Component
public class ScheduleOracle11gSQLDialectService extends AbstractSQLDialectService implements ScheduleSQLDialectService {

    private static final String LIMIT_PROPERTY = "pageSize";

    private static final String OFFSET_PROPERTY = "start";

    @Override
    public boolean isSupported(DialectVersion dialectVersion, MappedStatement mappedStatement) {
        return ScheduleDialectConstants.PRODUCT_ORACLE.equals(dialectVersion.getType()) &&
                ScheduleDialectConstants.ORACLE_11_MAJOR_VERSION.equals(dialectVersion.getMajorVersion());
    }

    @Override
    protected String getOriginType() {
        return JdbcUtils.MYSQL.name();
    }

    @Override
    protected DbType getTargetType() {
        return JdbcUtils.ORACLE;
    }

    @Override
    protected SQLASTVisitor getSQLVisitor(List<ResultMap> resultMaps) {
        return new Oracle11gSQLVisitor();
    }

    @Override
    public String resolve(String sql, BoundSql boundSql, List<ResultMap> resultMaps) {
        addOffset(boundSql);
        return super.resolve(sql, boundSql, resultMaps);
    }

    protected void addOffset(BoundSql boundSql) {
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        int limitIndex = -1, offsetIndex = -1;
        for (int i = 0; i < parameterMappings.size(); i++) {
            // ParameterMapping 只读
            ParameterMapping parameterMapping = parameterMappings.get(i);

            if (LIMIT_PROPERTY.equals(parameterMapping.getProperty())) {
                limitIndex = i;
            } else if (OFFSET_PROPERTY.equals(parameterMapping.getProperty())) {
                offsetIndex = i;
            }
        }
        if (limitIndex != -1 && offsetIndex != -1) {
            ParameterMapping offsetMapping = parameterMappings.get(offsetIndex);
            parameterMappings.add(offsetMapping);
        }
    }
}
