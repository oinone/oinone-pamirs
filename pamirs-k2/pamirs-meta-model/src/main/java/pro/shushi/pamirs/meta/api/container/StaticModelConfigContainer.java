package pro.shushi.pamirs.meta.api.container;

import com.alibaba.ttl.TransmittableThreadLocal;
import org.apache.commons.collections.CollectionUtils;
import pro.shushi.pamirs.meta.api.CommonApiFactory;
import pro.shushi.pamirs.meta.api.core.compute.systems.relation.RelationProcessor;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.domain.model.ModelField;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 静态模型配置容器
 * <p>
 * 2020/6/30 2:34 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class StaticModelConfigContainer {

    private static final Map<String, ModelConfig> staticModelConfigMap = new ConcurrentHashMap<>();

    private static final TransmittableThreadLocal<Map<String, ModelConfig>> transientModelConfigMap = new TransmittableThreadLocal<>();

    private static final Map<String, ModelConfig> baseModelConfigMap = new ConcurrentHashMap<>();

    public static ModelConfig getModelConfig(String model) {
        return staticModelConfigMap.get(model);
    }

    public static void setModelConfig(String model, ModelConfig modelConfig) {
        staticModelConfigMap.put(model, modelConfig);
    }

    public static void clear(String model) {
        staticModelConfigMap.remove(model);
    }

    public static void completeCompute() {
        for (ModelConfig modelConfig : staticModelConfigMap.values()) {
            List<ModelField> modelFieldList = modelConfig.getModelDefinition().getModelFields();
            if (CollectionUtils.isNotEmpty(modelFieldList)) {
                for (ModelField modelField : modelFieldList) {
                    modelField.construct(modelField);
                    if (modelField.getRelationStore()) {
                        CommonApiFactory.getApi(RelationProcessor.class)
                                .makeDefaultRelationReferenceFields(null, modelConfig.getModelDefinition(), modelField);
                    }
                }
            }
        }
    }

    public static ModelConfig getTransientModelConfig(String model) {
        return initTransientModelConfig().get(model);
    }

    public static void setTransientModelConfig(String model, ModelConfig modelConfig) {
        initTransientModelConfig().put(model, modelConfig);
    }

    public static void clearTransientModelConfig(String model) {
        initTransientModelConfig().remove(model);
    }

    private static Map<String, ModelConfig> initTransientModelConfig() {
        if (null == transientModelConfigMap.get()) {
            transientModelConfigMap.set(new HashMap<>());
        }
        return transientModelConfigMap.get();
    }

    public static ModelConfig getBaseModelConfig(String model) {
        return baseModelConfigMap.get(model);
    }

    public static void setBaseModelConfig(String model, ModelConfig modelConfig) {
        baseModelConfigMap.put(model, modelConfig);
    }

}
