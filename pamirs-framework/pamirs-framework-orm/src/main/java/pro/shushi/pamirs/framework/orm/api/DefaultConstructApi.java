package pro.shushi.pamirs.framework.orm.api;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.compute.definition.ValueComputer;
import pro.shushi.pamirs.meta.api.core.orm.convert.DataConverter;
import pro.shushi.pamirs.meta.api.core.orm.systems.ConstructApi;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 函数API实现
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 */
@Slf4j
@Component
public class DefaultConstructApi implements ConstructApi {

    @Resource
    private DataConverter persistenceDataConverter;

    @Override
    public <T> T construct(T data) {
        String model = Models.api().getModel(data);
        ModelConfig modelConfig = PamirsSession.getContext().getModelConfig(model);
        if (modelConfig == null) {
            log.error("Invalid model config. model: {}", model);
            return data;
        }
        // 计算字段默认值
        if (!CollectionUtils.isEmpty(modelConfig.getModelFieldConfigList())) {
            ValueComputer defaultValueComputer = ValueComputer.get();
            ValueComputer expressionDefaultValueComputer = ValueComputer.getExpressionComputer();
            List<ModelFieldConfig> lazyComputeFields = new ArrayList<>();
            for (ModelFieldConfig modelFieldConfig : modelConfig.getModelFieldConfigList()) {
                String defaultValue = modelFieldConfig.getDefaultValue();
                if (StringUtils.isNotBlank(defaultValue)) {
                    if (ValueComputer.isUsingExpressionComputer(defaultValue)) {
                        lazyComputeFields.add(modelFieldConfig);
                    } else {
                        defaultValueComputer.compute(modelFieldConfig, data);
                    }
                }
            }
            for (ModelFieldConfig modelFieldConfig : lazyComputeFields) {
                expressionDefaultValueComputer.compute(modelFieldConfig, data);
            }
        }
        return persistenceDataConverter.out(model, data);
    }

}
