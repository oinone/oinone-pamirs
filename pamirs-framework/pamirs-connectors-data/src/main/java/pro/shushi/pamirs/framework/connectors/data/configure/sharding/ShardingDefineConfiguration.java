package pro.shushi.pamirs.framework.connectors.data.configure.sharding;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import pro.shushi.pamirs.framework.common.constants.ConfigureConstants;
import pro.shushi.pamirs.framework.compute.InheritedComputeTemplate;
import pro.shushi.pamirs.framework.connectors.data.configure.sharding.model.ShardingDsDefinition;
import pro.shushi.pamirs.framework.connectors.data.configure.sharding.model.ShardingTableDefinition;
import pro.shushi.pamirs.framework.connectors.data.enmu.DataExpEnumerate;
import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * sharding分库分表定义配置
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-09 17:07
 */
@Data
@Configuration
@ConfigurationProperties(prefix = ConfigureConstants.SHARDING_DEFINE_CONFIG_PREFIX)
@RefreshScope
public class ShardingDefineConfiguration {

    private Map<String, List<String>> dataSources;

    private Map<String, ShardingDsDefinition> modules;

    private Map<String, ShardingTableDefinition> models;

    public ShardingDsDefinition getDefinitionForModule(String module) {
        return Optional.ofNullable(modules).map(v -> v.get(module)).orElse(null);
    }

    public ShardingTableDefinition getDefinitionForModel(String model) {
        ShardingTableDefinition tryGet = Optional.ofNullable(models).map(v -> v.get(model)).orElse(null);
        if (null != tryGet) {
            return tryGet;
        }
        ModelDefinition modelDefinition = Optional.ofNullable(PamirsSession.getContext().getModelConfig(model))
                .map(ModelConfig::getModelDefinition).orElse(null);
        if (null == modelDefinition) {
            return null;
        }
        final Result<String> rootStoreModelResult = new Result<>();
        InheritedComputeTemplate.compute(modelDefinition,
                iModel -> {
                    ModelConfig modelConfig = PamirsSession.getContext().getModelConfig(iModel);
                    if (null == modelConfig) {
                        throw PamirsException.construct(DataExpEnumerate.BASE_MODEL_INHERIT_DEPENDENT_ERROR)
                                .appendMsg("model:" + modelDefinition.getModel() + ", inheritModel:" + iModel).errThrow();
                    }
                    return modelConfig.getModelDefinition();
                },
                new HashMap<>(),
                null, null, true, null,
                null, null, null, null,
                (currentModel, superModel) -> {
                    if (!StringUtils.isBlank(currentModel.getMultiTable())
                            && currentModel.getMultiTable().equals(superModel.getModel())) {
                        return;
                    }
                    if (null == rootStoreModelResult.getData()) {
                        rootStoreModelResult.setData(superModel.getModel());
                    }
                }, null);
        String rootStoreModel = rootStoreModelResult.getData();
        if (StringUtils.isBlank(rootStoreModel)) {
            return null;
        }
        return Optional.ofNullable(models).map(v -> v.get(rootStoreModel)).orElse(null);
    }

    public boolean isSharding(String module, String model) {
        ShardingTableDefinition shardingTableDefinition = getDefinitionForModel(model);
        if (null != shardingTableDefinition) {
            return true;
        } else {
            ShardingDsDefinition shardingDsDefinition = getDefinitionForModule(module);
            return null != shardingDsDefinition && (null == shardingDsDefinition.getExcludeModels()
                    || !shardingDsDefinition.getExcludeModels().contains(model));
        }
    }

}
