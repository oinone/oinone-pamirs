package pro.shushi.pamirs.framework.configure.simulate.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.common.emnu.FwExpEnumerate;
import pro.shushi.pamirs.framework.common.utils.ObjectUtils;
import pro.shushi.pamirs.framework.configure.simulate.api.MetaSimulateService;
import pro.shushi.pamirs.meta.annotation.sys.MetaSimulator;
import pro.shushi.pamirs.meta.api.CommonApiFactory;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.Runner;
import pro.shushi.pamirs.meta.api.RunnerWithoutResult;
import pro.shushi.pamirs.meta.api.container.StaticModelConfigContainer;
import pro.shushi.pamirs.meta.api.core.configure.MetaModelFetcher;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.stl.ConcurrentHashSet;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelField;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static pro.shushi.pamirs.meta.annotation.sys.MetaSimulator.SIMULATE_PREFIX;

/**
 * 模块元数据服务实现
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/15 6:03 下午
 */
@Component
public class DefaultMetaSimulateService implements MetaSimulateService {

    private static volatile Map<String/*model*/, String/*simulate model*/> simulateModelMap;

    private final static Set<String/*model*/> preCreateTableModels = new ConcurrentHashSet<>();

    protected static Map<String, String> getSimulateModelMap() {
        if (simulateModelMap == null) {
            synchronized (DefaultMetaSimulateService.class) {
                if (simulateModelMap == null) {
                    Map<String, String> simulateModelMap = new HashMap<>();
                    Set<Class<?>> metaClasses = CommonApiFactory.getApi(MetaModelFetcher.class).fetchMetaClasses();
                    for (Class<?> metaClazz : metaClasses) {
                        MetaSimulator metaSimulator = AnnotationUtils.getAnnotation(metaClazz, MetaSimulator.class);
                        if (null != metaSimulator) {
                            String model = Models.api().getModel(metaClazz);
                            if (StringUtils.isBlank(model)) {
                                throw PamirsException.construct(FwExpEnumerate.BASE_META_SIMULATE_CONFIG_ERROR).errThrow();
                            }
                            String simulateModel = SIMULATE_PREFIX + model;
                            simulateModelMap.putIfAbsent(model, simulateModel);
                            if (metaSimulator.preCreateTable()) {
                                preCreateTableModels.add(model);
                            }
                        }
                    }
                    DefaultMetaSimulateService.simulateModelMap = simulateModelMap;
                }
            }
        }
        return simulateModelMap;
    }

    protected static Set<String> getPreCreateTableModels() {
        return preCreateTableModels;
    }

    @Override
    public void transientStaticExecuteWithoutResult(Map<String/*model*/, String/*simulate model*/> modelMap, RunnerWithoutResult runner) {
        transientStaticExecute(modelMap, () -> {
            runner.run();
            return null;
        });
    }

    @Override
    public <T> T transientStaticExecute(Map<String/*model*/, String/*simulate model*/> modelMap, Runner<T> runner) {
        T t;
        try {
            for (String model : modelMap.keySet()) {
                String simulateModel = modelMap.get(model);
                ModelDefinition modelDefinition = ObjectUtils.clone(PamirsSession.getContext().getModelConfig(simulateModel).getModelDefinition());
                for (ModelField modelField : modelDefinition.getModelFields()) {
                    modelField.setModel(model);
                }
                ModelConfig transientModelConfig = new ModelConfig(modelDefinition.setModel(model));
                StaticModelConfigContainer.setTransientModelConfig(model, transientModelConfig);
            }

            t = runner.run();
        } finally {
            for (String model : modelMap.keySet()) {
                StaticModelConfigContainer.clearTransientModelConfig(model);
            }
        }
        return t;
    }

}
