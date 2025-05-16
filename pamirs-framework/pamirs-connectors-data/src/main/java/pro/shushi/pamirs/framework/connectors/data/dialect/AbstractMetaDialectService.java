package pro.shushi.pamirs.framework.connectors.data.dialect;

import pro.shushi.pamirs.framework.connectors.data.configure.mapper.PamirsMapperConfiguration;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.model.PamirsDataConfiguration;

import javax.annotation.Resource;

/**
 * 方言服务抽象服务
 * <p>
 * 2020/7/16 1:47 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public abstract class AbstractMetaDialectService {

    @Resource
    private PamirsMapperConfiguration pamirsMapperConfiguration;

    public String dialectTableName(String dsKey, String tableName) {
        PamirsDataConfiguration pamirsDataConfiguration = pamirsMapperConfiguration.fetchPamirsDataConfiguration(dsKey);
        boolean tableNameCaseInsensitive = null == pamirsDataConfiguration || !pamirsDataConfiguration.isTableNameCaseSensitive();
        if (tableNameCaseInsensitive) {
            tableName = tableName.toLowerCase();
        }
        return tableName;
    }

}
