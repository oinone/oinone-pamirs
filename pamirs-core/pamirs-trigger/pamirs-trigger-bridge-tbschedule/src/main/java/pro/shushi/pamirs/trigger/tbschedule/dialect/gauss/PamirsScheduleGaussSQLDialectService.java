package pro.shushi.pamirs.trigger.tbschedule.dialect.gauss;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.middleware.schedule.core.dialect.ScheduleDialectConstants;
import pro.shushi.pamirs.middleware.schedule.core.dialect.entity.DialectVersion;
import pro.shushi.pamirs.middleware.schedule.core.dialect.gauss.ScheduleGaussSQLDialectService;

/**
 * GaussDB 脚本执行方言服务
 *
 * @author wangxian at 2024-03-29
 */
@Component
@Order(999)
public class PamirsScheduleGaussSQLDialectService extends ScheduleGaussSQLDialectService {

    @Override
    public boolean isSupported(DialectVersion dialectVersion, MappedStatement mappedStatement) {
        return ScheduleDialectConstants.PRODUCT_GAUSS_DB.equals(dialectVersion.getType()) &&
                !SqlCommandType.INSERT.equals(mappedStatement.getSqlCommandType());
    }
}
