package pro.shushi.pamirs.middleware.schedule.core.dialect.oracle11g;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.util.JdbcUtils;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ResultMap;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.middleware.schedule.core.dialect.AbstractSQLDialectService;
import pro.shushi.pamirs.middleware.schedule.core.dialect.ScheduleDialectConstants;
import pro.shushi.pamirs.middleware.schedule.core.dialect.ScheduleSQLDialectService;
import pro.shushi.pamirs.middleware.schedule.core.dialect.entity.DialectVersion;
import pro.shushi.pamirs.middleware.schedule.core.dialect.visitor.Oracle11gSQLVisitor;

import java.util.List;

/**
 * ORACLE脚本执行方言服务
 *
 * @author Adamancy Zhang at 21:50 on 2023-06-27
 */
@Order(88)
@Component
public class ScheduleOracle11gSQLDialectService extends AbstractSQLDialectService implements ScheduleSQLDialectService {

    private static final String LIMIT_PROPERTY = "pageSize";

    private static final String OFFSET_PROPERTY = "start";

    @Override
    public boolean isSupported(DialectVersion dialectVersion, MappedStatement mappedStatement) {
        return ScheduleDialectConstants.PRODUCT_ORACLE.equals(dialectVersion.getType()) &&
                (ScheduleDialectConstants.ORACLE_11_VERSION.equals(dialectVersion.getVersion()) || ScheduleDialectConstants.ORACLE_11_MAJOR_VERSION.equals(dialectVersion.getMajorVersion()));
    }

    @Override
    protected String getOriginType() {
        return JdbcUtils.MYSQL;
    }

    @Override
    protected String getTargetType() {
        return JdbcUtils.ORACLE;
    }

    @Override
    protected SQLASTVisitor getSQLVisitor(List<ResultMap> resultMaps) {
        return new Oracle11gSQLVisitor();
    }

    @Override
    public String resolve(String sql, BoundSql boundSql, List<ResultMap> resultMaps) {
        addOffset(boundSql);
        List<SQLStatement> statements = SQLUtils.parseStatements(sql, getOriginType());
        SQLASTVisitor visitor = getSQLVisitor(resultMaps);
        for (SQLStatement statement : statements) {
            statement.accept(visitor);
        }
        return SQLUtils.toSQLString(statements, getTargetType(), getFormatOption());
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

    public static void main(String[] args) {
        String sql = "select\n" +
                "         \n" +
                "        `id`,\n" +
                "        `technical_name`,\n" +
                "        `table_num`,\n" +
                "        `task_type`,\n" +
                "        `remark`,\n" +
                "        `interface_name`,\n" +
                "        `method_name`,\n" +
                "        `version`,\n" +
                "        `group`,\n" +
                "        `timeout`,\n" +
                "        `tenant`,\n" +
                "        `env`,\n" +
                "        `user_id`,\n" +
                "        `username`,\n" +
                "        `own_sign`,\n" +
                "        `application`,\n" +
                "        `biz_id`,\n" +
                "        `parent_biz_id`,\n" +
                "        `biz_code`,\n" +
                "        `context`,\n" +
                "        `limit_execute_number`,\n" +
                "        `is_cycle`,\n" +
                "        `cron`,\n" +
                "        `period_time_value`,\n" +
                "        `period_time_unit`,\n" +
                "        `period_time_anchor`,\n" +
                "        `limit_retry_number`,\n" +
                "        `next_retry_time_value`,\n" +
                "        `next_retry_time_unit`,\n" +
                "        `execute_number`,\n" +
                "        `anchor_execute_time`,\n" +
                "        `next_execute_time`,\n" +
                "        `last_execute_time`,\n" +
                "        `retry_number`,\n" +
                "        `task_status`,\n" +
                "        `is_transfer`,\n" +
                "        `is_canceled`,\n" +
                "        `error_log`,\n" +
                "        `create_date`,\n" +
                "        `write_date`\n" +
                "     \n" +
                "        from `pamirs_schedule_2` \n" +
                "        where `is_deleted` = 0 and `task_status` = 0 and `is_transfer` = 0 and `is_canceled` = 0 and `table_num` = ?\n" +
                "        and   `next_execute_time` < ?  \n" +
                "        and mod(biz_id % 10000,?) in\n" +
                "         (  \n" +
                "            ?\n" +
                "         ) \n" +
                "         \n" +
                "            and task_type = ?\n" +
                "         \n" +
                "         \n" +
                "         \n" +
                "         \n" +
                "         \n" +
                "            and `own_sign` = ?\n" +
                "         \n" +
                "         \n" +
                "         \n" +
                "         \n" +
                "            order by next_execute_time\n" +
                "         \n" +
                "         \n" +
                "            limit ? offset ?";
        System.out.println(new ScheduleOracle11gSQLDialectService().resolve(sql, null, null));
    }
}
