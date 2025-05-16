package pro.shushi.pamirs.framework.compute.definition.model;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.definition.model.ModelDefinitionManager;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.session.PamirsSession;

import java.util.List;
import java.util.Map;

/**
 * 默认模型管理接口实现
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/4 2:48 上午
 */
@SuppressWarnings("unused")
@Slf4j
@Component
public class DefaultModelDefinitionManager implements ModelDefinitionManager {

    @Override
    public void dealInheritedForClient(ModelConfig modelConfig, List<Function> functionList, Function function) {

    }

    @Override
    public int countNonEmptyModelFieldSizeFromDMap(String model, Map<String, Object> data) {
        int count = 0;
        ModelConfig modelConfig = PamirsSession.getContext().getModelConfig(model);
        for (ModelFieldConfig modelFieldConfig : modelConfig.getModelFieldConfigList()) {
            if (null != data.get(modelFieldConfig.getLname())) {
                count++;
            }
        }
        return count;
    }

}
