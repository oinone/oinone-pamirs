package pro.shushi.pamirs.trigger.tbschedule.dialect.dm;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.middleware.schedule.core.dialect.ScheduleDialectConstants;
import pro.shushi.pamirs.middleware.schedule.core.dialect.dm.ScheduleDMSQLDialectService;
import pro.shushi.pamirs.middleware.schedule.core.dialect.entity.DialectVersion;

/**
 * DM Insert 脚本执行方言服务
 *
 * @author Adamancy Zhang at 12:06 on 2023-06-28
 */
@Component
@Order(999)
public class PamirsScheduleDMSQLInsertDialectService extends ScheduleDMSQLDialectService {

    @Override
    public boolean isSupported(DialectVersion dialectVersion, MappedStatement mappedStatement) {
        return ScheduleDialectConstants.PRODUCT_DM.equals(dialectVersion.getType()) &&
                SqlCommandType.INSERT.equals(mappedStatement.getSqlCommandType());
    }
}
