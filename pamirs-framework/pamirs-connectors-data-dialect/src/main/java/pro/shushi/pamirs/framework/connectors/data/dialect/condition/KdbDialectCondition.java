package pro.shushi.pamirs.framework.connectors.data.dialect.condition;

import pro.shushi.pamirs.meta.enmu.DataSourceEnum;

/**
 * @author Adamancy Zhang at 16:17 on 2026-02-26
 */
public class KdbDialectCondition extends AbstractDialectCondition {
    @Override
    protected DataSourceEnum getDataSource() {
        return DataSourceEnum.KDB;
    }
}
