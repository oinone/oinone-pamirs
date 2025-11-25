package pro.shushi.pamirs.framework.connectors.data.mapper.method.spi;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.mapper.template.SqlTemplate;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * 逻辑列fillSqlSegment
 *
 * @author wx@shushi.pro
 * @version 1.0.0
 * 2025/11/19
 */
@SPI.Service
@Component
@Order(Integer.MAX_VALUE) //默认优先级最低，业务配置需要配置成为优先级高
public class DefaultLogicColumnSqlApi implements LogicColumnSqlApi {

    @Override
    public String LogicColumnScript(String model, String column, String property) {
        return String.format(SqlTemplate.EQ_CONDITION, column, property);
    }
}
