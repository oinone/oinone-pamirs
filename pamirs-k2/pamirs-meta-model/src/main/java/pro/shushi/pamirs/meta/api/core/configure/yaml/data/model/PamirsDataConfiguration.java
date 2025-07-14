package pro.shushi.pamirs.meta.api.core.configure.yaml.data.model;

import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.constant.ExpressionConstants;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * pamirs数据持久层ORM配置信息
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/14 11:03 上午
 */
@Data
public class PamirsDataConfiguration implements Serializable {

    private static final long serialVersionUID = -768076558542708401L;

    private String databaseFormat;

    private String tableFormat;

    private String tablePattern = ExpressionConstants.S_PLACEHOLDER;

    private String columnPattern = ExpressionConstants.S_PLACEHOLDER;

    private PamirsTableInfo tableInfo;

    private boolean tableNameCaseSensitive = false;

    private Set<String> deprecatedColumns;

    private Map<String, Object> configuration;

}
