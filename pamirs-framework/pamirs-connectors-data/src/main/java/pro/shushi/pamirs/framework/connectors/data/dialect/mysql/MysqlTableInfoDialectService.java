package pro.shushi.pamirs.framework.connectors.data.dialect.mysql;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.dialect.api.Dialect;
import pro.shushi.pamirs.framework.connectors.data.dialect.api.TableInfoDialectService;
import pro.shushi.pamirs.framework.connectors.data.dialect.mysql.constants.MySQLTableInfoConstants;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.model.PamirsTableInfo;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * 表配置 方言服务
 * <p>
 * 2023/06/25
 *
 * @author wx@shushi.pro
 * @version 1.0.0
 */
@Slf4j
@Dialect.component
@SPI.Service
@Component
public class MysqlTableInfoDialectService implements TableInfoDialectService {

    @Override
    public void fillDefaultConfig(String dsKey, PamirsTableInfo pamirsTableInfo) {
        if (null == pamirsTableInfo.getLogicDelete()) {
            pamirsTableInfo.setLogicDelete(MySQLTableInfoConstants.DEFAULT_LOGIC_DELETE);
        }
        if (StringUtils.isBlank(pamirsTableInfo.getLogicDeleteColumn())) {
            pamirsTableInfo.setLogicDeleteColumn(MySQLTableInfoConstants.DEFAULT_LOGIC_DELETE_COLUMN);
        }
        if (StringUtils.isBlank(pamirsTableInfo.getLogicDeleteValue())) {
            pamirsTableInfo.setLogicDeleteValue(MySQLTableInfoConstants.DEFAULT_LOGIC_DELETE_VALUE);
        }
        if (StringUtils.isBlank(pamirsTableInfo.getLogicNotDeleteValue())) {
            pamirsTableInfo.setLogicNotDeleteValue(MySQLTableInfoConstants.DEFAULT_LOGIC_NOT_DELETE_VALUE);
        }
        if (StringUtils.isBlank(pamirsTableInfo.getKeyGenerator())) {
            pamirsTableInfo.setKeyGenerator(MySQLTableInfoConstants.DEFAULT_KEY_GENERATOR_VALUE);
        }
        if (null == pamirsTableInfo.getCapitalMode()) {
            pamirsTableInfo.setCapitalMode(MySQLTableInfoConstants.DEFAULT_CAPITAL_MODE);
        }
        if (null == pamirsTableInfo.getUnderCamel()) {
            pamirsTableInfo.setUnderCamel(MySQLTableInfoConstants.DEFAULT_UNDER_CAMEL);
        }
        if (StringUtils.isBlank(pamirsTableInfo.getTableFormat())) {
            pamirsTableInfo.setTableFormat(MySQLTableInfoConstants.DEFAULT_TABLE_FORMAT);
        }
        if (StringUtils.isBlank(pamirsTableInfo.getColumnFormat())) {
            pamirsTableInfo.setColumnFormat(MySQLTableInfoConstants.DEFAULT_COLUMN_FORMAT);
        }
        if (StringUtils.isBlank(pamirsTableInfo.getAliasFormat())) {
            pamirsTableInfo.setAliasFormat(MySQLTableInfoConstants.DEFAULT_ALIAS_FORMAT);
        }
        if (StringUtils.isBlank(pamirsTableInfo.getCharset())) {
            pamirsTableInfo.setCharset(MySQLTableInfoConstants.DEFAULT_CHARSET);
        }
        if (StringUtils.isBlank(pamirsTableInfo.getCollate())) {
            pamirsTableInfo.setCollate(MySQLTableInfoConstants.DEFAULT_COLLATE);
        }
    }
}
