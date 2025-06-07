package pro.shushi.pamirs.trigger.tbschedule.dialect.postgresql;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.middleware.schedule.core.dialect.ScheduleDialectConstants;
import pro.shushi.pamirs.middleware.schedule.core.dialect.entity.DialectVersion;
import pro.shushi.pamirs.middleware.schedule.core.dialect.postgresql.SchedulePostgreSQLDialectService;

/**
 * PostgreSQL脚本执行方言服务
 *
 * @author Adamancy Zhang at 11:50 on 2023-06-28
 */
@Component
@Order(999)
public class PamirsSchedulePostgreSQLDialectService extends SchedulePostgreSQLDialectService {

    @Override
    public boolean isSupported(DialectVersion dialectVersion, MappedStatement mappedStatement) {
        return ScheduleDialectConstants.PRODUCT_POSTGRE_SQL.equals(dialectVersion.getType()) &&
                !SqlCommandType.INSERT.equals(mappedStatement.getSqlCommandType());
    }
}
