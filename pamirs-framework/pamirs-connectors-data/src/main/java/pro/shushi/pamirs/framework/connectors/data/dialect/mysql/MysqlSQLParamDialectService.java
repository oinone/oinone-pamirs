package pro.shushi.pamirs.framework.connectors.data.dialect.mysql;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.dialect.api.Dialect;
import pro.shushi.pamirs.framework.connectors.data.dialect.api.SQLParamDialectService;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.util.Map;

import static pro.shushi.pamirs.meta.constant.FieldConstants.WRITE_DATE;

/**
 * 脚本执行方言服务，WriteDate等参数处理
 *
 * @author wangxian 2023-06-29
 */
@Dialect.component
@SPI.Service
@Component
public class MysqlSQLParamDialectService implements SQLParamDialectService {

    @Override
    public void resolveIfWriteDate(Map<String, Object> map) {
        map.remove(WRITE_DATE, null);
    }

    @Override
    public void resolveWriteDate(Map<String, Object> map) {
        map.remove(WRITE_DATE);
    }
}
