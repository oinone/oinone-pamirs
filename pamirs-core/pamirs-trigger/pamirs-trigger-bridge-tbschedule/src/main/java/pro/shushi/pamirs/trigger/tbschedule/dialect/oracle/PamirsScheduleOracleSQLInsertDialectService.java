package pro.shushi.pamirs.trigger.tbschedule.dialect.oracle;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.middleware.schedule.core.dialect.ScheduleDialectConstants;
import pro.shushi.pamirs.middleware.schedule.core.dialect.entity.DialectVersion;
import pro.shushi.pamirs.middleware.schedule.core.dialect.oracle.ScheduleOracleSQLDialectService;

/**
 * ORACLE Insert 脚本执行方言服务
 *
 * @author Adamancy Zhang at 11:52 on 2023-06-28
 */
@Component
@Order(999)
public class PamirsScheduleOracleSQLInsertDialectService extends ScheduleOracleSQLDialectService {

    @Override
    public boolean isSupported(DialectVersion dialectVersion, MappedStatement mappedStatement) {
        return ScheduleDialectConstants.PRODUCT_ORACLE.equals(dialectVersion.getType()) &&
                SqlCommandType.INSERT.equals(mappedStatement.getSqlCommandType());
    }
}
