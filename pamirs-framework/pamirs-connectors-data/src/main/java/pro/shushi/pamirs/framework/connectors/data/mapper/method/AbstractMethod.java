package pro.shushi.pamirs.framework.connectors.data.mapper.method;

import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;

import java.util.List;

/**
 * 通用 SQL Statement  生成方法抽象类
 * <p>
 * 2020/6/16 1:39 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class AbstractMethod {

    private final ModelConfig modelConfig;

    public AbstractMethod(ModelConfig modelConfig) {
        this.modelConfig = modelConfig;
    }

    public ModelConfig getModelConfig() {
        return this.modelConfig;
    }

    public List<ModelFieldConfig> sqlMethodTableFieldConfigList() {
        return getModelConfig().getSqlMethodModelFieldConfigList();
    }

}
