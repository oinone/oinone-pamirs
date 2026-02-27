package pro.shushi.pamirs.framework.connectors.data.dialect.condition;

import org.springframework.context.annotation.Condition;
import pro.shushi.pamirs.meta.enmu.DataSourceEnum;

/**
 * @author Adamancy Zhang at 16:16 on 2026-02-26
 */
public class OracleDialectCondition extends AbstractDialectCondition implements Condition {

    @Override
    protected DataSourceEnum getDataSource() {
        return DataSourceEnum.ORACLE;
    }
}
