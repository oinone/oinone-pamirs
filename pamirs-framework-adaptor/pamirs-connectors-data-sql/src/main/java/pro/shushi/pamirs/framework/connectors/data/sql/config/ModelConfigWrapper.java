package pro.shushi.pamirs.framework.connectors.data.sql.config;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.model.PamirsTableInfo;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.prefix.DataPrefixManager;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

import java.util.function.Predicate;

import static java.util.stream.Collectors.joining;

/**
 * 模型持久层包装类
 * <p>
 * 2020/6/16 1:57 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Data
public class ModelConfigWrapper {

    private ModelConfig modelConfig;

    public static ModelConfigWrapper wrap(ModelConfig modelConfig) {
        return new ModelConfigWrapper().setModelConfig(modelConfig);
    }

    /**
     * 获取需要进行查询的 select sql 片段
     *
     * @param predicate 过滤条件
     * @return sql 片段
     */
    public String chooseSelect(Predicate<ModelFieldConfig> predicate) {
        String fieldsSqlSelect = this.getModelConfig().getModelFieldConfigList().stream().filter(ModelFieldConfig::getStore).filter(predicate)
                .map(v -> ModelFieldConfigWrapper.wrap(v).getSqlSelect()).collect(joining(CharacterConstants.SEPARATOR_COMMA));
        if (StringUtils.isNotBlank(fieldsSqlSelect)) {
            return fieldsSqlSelect;
        }
        return CharacterConstants.SEPARATOR_ASTERISK;
    }

    public String getTable() {
        PamirsTableInfo pamirsTableInfo = PamirsTableInfo.fetchPamirsTableInfo(modelConfig.getModel());
        String tableFormat = pamirsTableInfo.getTableFormat();
        String module = modelConfig.getModule();
        String model = modelConfig.getModel();
        String table = DataPrefixManager.tablePrefix(module, model, this.getModelConfig().getTable());
        if (StringUtils.isNotBlank(tableFormat)) {
            return String.format(tableFormat, table);
        }
        return table;
    }

}
