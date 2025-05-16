package pro.shushi.pamirs.framework.connectors.data.ddl.dialect.mysql;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.api.domain.wrapper.ModelWrapper;
import pro.shushi.pamirs.framework.connectors.data.ddl.dialect.api.TableDialectComponent;
import pro.shushi.pamirs.framework.connectors.data.dialect.api.Dialect;
import pro.shushi.pamirs.framework.connectors.data.dialect.constants.DataProductVersion;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

/**
 * 表操作组件
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/19 2:16 上午
 */
@Order(88)
@Dialect.component(version = DataProductVersion.DEFAULT_MYSQL_NDS_VERSION)
@Component
public class MysqlNdsTableComponent extends MysqlTableComponent implements TableDialectComponent {

    @Override
    public String lock(ModelWrapper modelDefinition) {
        return CharacterConstants.SEPARATOR_EMPTY;
    }

    @Override
    public String unlock() {
        return CharacterConstants.SEPARATOR_EMPTY;
    }
}
