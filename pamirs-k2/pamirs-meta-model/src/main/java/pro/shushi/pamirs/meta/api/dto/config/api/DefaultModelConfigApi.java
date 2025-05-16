package pro.shushi.pamirs.meta.api.dto.config.api;

import com.google.common.collect.Lists;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 模型配置扩展默认实现
 *
 * @author wx@shushi.pro
 * @version 1.0.0
 * date 2024/03/04
 */
@Order
@Component
@SPI.Service
public class DefaultModelConfigApi implements ModelConfigApi {

    @Override
    public List<ModelFieldConfig> getModelFieldConfigList(ModelConfig modelConfig) {
        List<ModelFieldConfig> modelFieldConfigList = modelConfig.getModelDefinition().getModelFields().stream()
                .map(ModelFieldConfig::new)
                .collect(Collectors.toList());
        extendLoad((api) -> api.getModelFieldConfigList(modelConfig, modelFieldConfigList));
        return modelFieldConfigList;
    }

    @Override
    public List<ModelFieldConfig> getModelFieldConfigListSort(ModelConfig modelConfig) {
        List<ModelFieldConfig> modelFieldConfigList = modelConfig.getModelDefinition().getModelFields().stream()
                .sorted(Comparator.comparingInt(field -> {
                    String value = field.getTtype().value();
                    if (TtypeEnum.isBasicType(value)) {
                        return 0;
                    } else if (TtypeEnum.ENUM.value().equals(value)) {
                        return 1;
                    } else if (TtypeEnum.isRelationType(value)) {
                        return 2;
                    } else {
                        return 3;
                    }
                }))
                .map(ModelFieldConfig::new)
                .collect(Collectors.toList());
        extendLoad((api) -> api.getModelFieldConfigListSort(modelConfig, modelFieldConfigList));
        return modelFieldConfigList;
    }

    @Override
    public List<String> getModels(ModelConfig modelConfig) {
        List<String> models = Lists.newArrayList(modelConfig.getModel());
        extendLoad((api) -> api.getModels(modelConfig, models));
        return models;
    }

    @Override
    public List<ModelFieldConfig> getSqlMethodModelFieldConfigList(ModelConfig modelConfig) {
        List<ModelFieldConfig> modelFieldConfigList = modelConfig.getModelDefinition().getModelFields().stream()
                .filter(ModelField::getStore)
                .map(ModelFieldConfig::new)
                .collect(Collectors.toList());
        extendLoad((api) -> api.getSqlMethodModelFieldConfigList(modelConfig, modelFieldConfigList));
        return modelFieldConfigList;
    }

    protected void extendLoad(Consumer<ModelConfigExtendApi> consumer) {
        for (ModelConfigExtendApi extendApi : Spider.getLoader(ModelConfigExtendApi.class).getOrderedExtensions()) {
            consumer.accept(extendApi);
        }
    }
}
