package pro.shushi.pamirs.middleware.schedule.core.dialect.dm;

import org.apache.ibatis.mapping.MappedStatement;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.middleware.schedule.core.dialect.ScheduleDialectConstants;
import pro.shushi.pamirs.middleware.schedule.core.dialect.entity.DialectVersion;
import pro.shushi.pamirs.middleware.schedule.core.dialect.oracle.ScheduleOracleSQLDialectService;

/**
 * DM脚本执行方言服务
 *
 * @author Adamancy Zhang at 22:00 on 2023-06-27
 */
@Order
@Component
public class ScheduleDMSQLDialectService extends ScheduleOracleSQLDialectService {

    @Override
    public boolean isSupported(DialectVersion dialectVersion, MappedStatement mappedStatement) {
        return ScheduleDialectConstants.PRODUCT_DM.equals(dialectVersion.getType());
    }
}
