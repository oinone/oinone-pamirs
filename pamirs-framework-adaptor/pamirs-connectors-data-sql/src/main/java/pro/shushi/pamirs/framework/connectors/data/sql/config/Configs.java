package pro.shushi.pamirs.framework.connectors.data.sql.config;

import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;

/**
 * 配置wrapper构造器
 * <p>
 * 2020/6/22 7:42 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class Configs {

    public static ModelConfigWrapper wrap(ModelConfig modelConfig) {
        return ModelConfigWrapper.wrap(modelConfig);
    }

    public static ModelFieldConfigWrapper wrap(ModelFieldConfig modelFieldConfig) {
        return ModelFieldConfigWrapper.wrap(modelFieldConfig);
    }

}
