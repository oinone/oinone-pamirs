package pro.shushi.pamirs.meta.api.core.configure.yaml.data;

import pro.shushi.pamirs.meta.api.core.configure.yaml.data.model.BatchOperation;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.model.PamirsDataConfiguration;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.model.PamirsTableConfig;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.model.PamirsTableInfo;
import pro.shushi.pamirs.meta.api.enmu.BatchCommitTypeEnum;

import java.util.Map;

/**
 * 持久层ORM配置代理
 * <p>
 * 2020/6/22 8:37 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public interface PamirsMapperConfigurationProxy {

    PamirsDataConfiguration fetchPamirsDataConfiguration(String dsKey);

    PamirsTableConfig fetchPamirsTableConfig(String dsKey);

    TableNameComputer fetchTableNameComputer();

    DynamicDsKeyComputer fetchDynamicDsKeyComputer();

    BatchCommitTypeEnum batch();

    Map<String/*model*/, BatchOperation> batchConfig();

    BatchOperation batchOperationForModel(String model);

    void fillDefaultConfig(String dsKey, PamirsTableInfo pamirsTableInfo);

}
