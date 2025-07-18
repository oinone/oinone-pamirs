package pro.shushi.pamirs.trigger.tbschedule.dialect.oracle;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlCommandType;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.middleware.schedule.core.dialect.ScheduleDialectConstants;
import pro.shushi.pamirs.middleware.schedule.core.dialect.entity.DialectVersion;
import pro.shushi.pamirs.middleware.schedule.core.dialect.oracle.ScheduleOracleSQLDialectService;

import java.util.List;

/**
 * ORACLE脚本执行方言服务
 *
 * @author Adamancy Zhang at 11:50 on 2023-06-28
 */
@Component
@Order(999)
public class PamirsScheduleOracleSQLDialectService extends ScheduleOracleSQLDialectService {

    @Override
    public boolean isSupported(DialectVersion dialectVersion, MappedStatement mappedStatement) {
        return ScheduleDialectConstants.PRODUCT_ORACLE.equals(dialectVersion.getType()) &&
                !SqlCommandType.INSERT.equals(mappedStatement.getSqlCommandType());
    }

    @Override
    public String resolve(String sql, BoundSql boundSql, List<ResultMap> resultMaps) {
        return sql;
    }
}
