package pro.shushi.pamirs.meta.api.core.configure.yaml.data.model;

import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.common.constants.TableInfoDefaultValueConstants;

/**
 * 表配置
 * <p>
 * 2022/10/24 4:14 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Data
public class PamirsTableConfig extends PamirsTableInfo {

    private static final long serialVersionUID = 5379367782195255454L;

    private Boolean tableNameCaseSensitive;

    @Override
    public PamirsTableConfig defaultValue(String dsKey) {
        super.defaultValue(dsKey);
        if (null == this.getTableNameCaseSensitive()) {
            this.setTableNameCaseSensitive(TableInfoDefaultValueConstants.DEFAULT_TABLE_NAME_CASE_SENSITIVE);
        }
        return this;
    }

}
