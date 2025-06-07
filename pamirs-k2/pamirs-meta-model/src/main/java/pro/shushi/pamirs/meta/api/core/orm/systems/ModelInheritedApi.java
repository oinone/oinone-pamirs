package pro.shushi.pamirs.meta.api.core.orm.systems;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * 模型继承计算
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/19 2:16 上午
 */
@SPI
public interface ModelInheritedApi {

    /**
     * 判断是否是传递父模型
     *
     * @param model       当前模型
     * @param parentModel 父模型
     * @return 是否父模型
     */
    default boolean isPropagationSuperModel(String model, String parentModel) {
        return isPropagationSuperModel(model, parentModel, null);
    }

    default boolean isPropagationSuperModel(String model, String parentModel, Function<String, ModelDefinition> modelFetcher) {
        if (null == parentModel) {
            return false;
        }
        return recursion(model, (currentModel, superModel) -> parentModel.equals(superModel), modelFetcher);
    }

    /**
     * 是否是传递扩展继承
     *
     * @param model       当前模型
     * @param parentModel 父模型
     * @return 是否是扩展继承
     */
    default boolean isPropagationExtendInherited(String model, String parentModel) {
        return isPropagationExtendInherited(model, parentModel, null);
    }

    default boolean isPropagationExtendInherited(String model, String parentModel, Function<String, ModelDefinition> modelFetcher) {
        if (null == parentModel) {
            return false;
        }
        return recursion(model, (currentModel, currentParent) -> {
            Boolean extendInheritedCondition = extendInheritedCondition(currentModel, currentParent, modelFetcher);
            if (null == extendInheritedCondition) {
                return null;
            }
            return currentParent.equals(parentModel);
        }, modelFetcher);
    }

    /**
     * 是否是相邻继承
     *
     * @param model       当前模型
     * @param parentModel 父模型
     * @return 是否是多表继承
     */
    default boolean isSuperModel(String model, String parentModel) {
        return isSuperModel(model, parentModel, null);
    }

    default boolean isSuperModel(String model, String parentModel, Function<String, ModelDefinition> modelFetcher) {
        ModelDefinition modelConfig = fetchSuperModel(model, parentModel, modelFetcher);
        return modelConfig != null;
    }

    /**
     * 是否是相邻多表继承
     *
     * @param model       当前模型
     * @param parentModel 父模型
     * @return 是否是多表继承
     */
    default boolean isMultiTableInherited(String model, String parentModel) {
        return isMultiTableInherited(model, parentModel, null);
    }

    default boolean isMultiTableInherited(String model, String parentModel, Function<String, ModelDefinition> modelFetcher) {
        ModelDefinition modelConfig = fetchSuperModel(model, parentModel, modelFetcher);
        if (modelConfig == null) return false;
        return parentModel.equals(modelConfig.getMultiTable());
    }

    /**
     * 是否是相邻扩展继承
     *
     * @param model       当前模型
     * @param parentModel 父模型
     * @return 是否是扩展继承
     */
    default boolean isExtendInherited(String model, String parentModel) {
        return isExtendInherited(model, parentModel, null);
    }

    default boolean isExtendInherited(String model, String parentModel, Function<String, ModelDefinition> modelFetcher) {
        ModelDefinition modelConfig = fetchSuperModel(model, parentModel, modelFetcher);
        if (modelConfig == null) return false;
        return isPropagationExtendInherited(model, parentModel, modelFetcher);
    }

    /**
     * 是否是相邻抽象继承
     *
     * @param model       当前模型
     * @param parentModel 父模型
     * @return 是否是抽象继承
     */
    default boolean isAbstractInherited(String model, String parentModel) {
        return isAbstractInherited(model, parentModel, null);
    }

    default boolean isAbstractInherited(String model, String parentModel, Function<String, ModelDefinition> modelFetcher) {
        if (null == modelFetcher) {
            modelFetcher = m -> Optional.ofNullable(PamirsSession.getContext())
                    .map(v -> v.getModelConfig(m)).map(ModelConfig::getModelDefinition).orElse(null);
        }
        if (!isSuperModel(model, parentModel, modelFetcher)) {
            return false;
        }
        ModelDefinition superModelConfig = modelFetcher.apply(parentModel);
        if (null == superModelConfig) {
            return false;
        }
        return ModelTypeEnum.ABSTRACT.equals(superModelConfig.getType());
    }

    /**
     * 是否是相邻代理继承
     *
     * @param model       当前模型
     * @param parentModel 父模型
     * @return 是否是代理继承
     */
    default boolean isProxyInherited(String model, String parentModel) {
        return isProxyInherited(model, parentModel, null);
    }

    default boolean isProxyInherited(String model, String parentModel, Function<String, ModelDefinition> modelFetcher) {
        if (null == modelFetcher) {
            modelFetcher = m -> Optional.ofNullable(PamirsSession.getContext())
                    .map(v -> v.getModelConfig(m)).map(ModelConfig::getModelDefinition).orElse(null);
        }
        if (!isSuperModel(model, parentModel, modelFetcher)) {
            return false;
        }
        ModelDefinition superModelConfig = modelFetcher.apply(parentModel);
        if (null == superModelConfig) {
            return false;
        }
        return ModelTypeEnum.PROXY.equals(superModelConfig.getType());
    }

    /**
     * 获取相邻继承模型配置
     *
     * @param model       当前模型
     * @param parentModel 父模型
     * @return 当前模型配置
     */
    default ModelDefinition fetchSuperModel(String model, String parentModel) {
        return fetchSuperModel(model, parentModel, null);
    }

    default ModelDefinition fetchSuperModel(String model, String parentModel, Function<String, ModelDefinition> modelFetcher) {
        if (null == modelFetcher) {
            modelFetcher = m -> Optional.ofNullable(PamirsSession.getContext())
                    .map(v -> v.getModelConfig(m)).map(ModelConfig::getModelDefinition).orElse(null);
        }
        ModelDefinition modelConfig = modelFetcher.apply(model);
        if (null == modelConfig) {
            return null;
        }
        List<String> superModels = modelConfig.getSuperModels();
        if (!CollectionUtils.isEmpty(superModels)) {
            if (!superModels.contains(parentModel)) {
                return null;
            }
        } else {
            return null;
        }
        return modelConfig;
    }

    /**
     * 是否是扩展继承自同一父模型
     *
     * @param model 当前模型
     * @param other 比较模型
     * @return 是否是扩展继承自同一父模型
     */
    default boolean isSameExtendSuperModel(String model, String other) {
        return isSameExtendSuperModel(model, other, null);
    }

    default boolean isSameExtendSuperModel(String model, String other, Function<String, ModelDefinition> modelFetcher) {
        Set<String> modelSuperModels = new HashSet<>();
        Set<String> otherSuperModels = new HashSet<>();
        fetchSuperModels(model, modelSuperModels, modelFetcher);
        fetchSuperModels(other, otherSuperModels, modelFetcher);
        modelSuperModels.retainAll(otherSuperModels);
        return modelSuperModels.size() > 0;
    }

    default void fetchSuperModels(String model, Set<String> modelSuperModels) {
        fetchSuperModels(model, modelSuperModels, null);
    }

    default void fetchSuperModels(String model, Set<String> modelSuperModels, Function<String, ModelDefinition> modelFetcher) {
        modelSuperModels.add(model);
        recursion(model, (currentModel, currentParent) -> {
            Boolean extendInheritedCondition = extendInheritedCondition(currentModel, currentParent, modelFetcher);
            if (null == extendInheritedCondition) {
                return null;
            }
            modelSuperModels.add(currentParent);
            return false;
        }, modelFetcher);
    }

    default ModelDefinition fetchProxyStoreModel(String proxyModel) {
        return fetchProxyStoreModel(proxyModel, null);
    }

    default ModelDefinition fetchProxyStoreModel(String proxyModel, Function<String, ModelDefinition> modelFetcher) {
        if (null == modelFetcher) {
            modelFetcher = m -> Optional.ofNullable(PamirsSession.getContext())
                    .map(v -> v.getModelConfig(m)).map(ModelConfig::getModelDefinition).orElse(null);
        }
        ModelDefinition proxyModelConfig = modelFetcher.apply(proxyModel);
        if (null == proxyModelConfig || !ModelTypeEnum.PROXY.equals(proxyModelConfig.getType())) {
            return proxyModelConfig;
        } else {
            return fetchProxyStoreModel(proxyModelConfig.getProxy(), modelFetcher);
        }
    }

    default Boolean extendInheritedCondition(String model, String currentParent) {
        return extendInheritedCondition(model, currentParent, null);
    }

    default Boolean extendInheritedCondition(String model, String currentParent, Function<String, ModelDefinition> modelFetcher) {
        if (null == modelFetcher) {
            modelFetcher = m -> Optional.ofNullable(PamirsSession.getContext())
                    .map(v -> v.getModelConfig(m)).map(ModelConfig::getModelDefinition).orElse(null);
        }

        ModelDefinition modelConfig = Optional.ofNullable(modelFetcher.apply(model)).orElse(null);
        if (null == modelConfig) {
            return null;
        }
        if (!ModelTypeEnum.STORE.equals(modelConfig.getType())) {
            return null;
        }
        ModelDefinition currentParentConfig = Optional.ofNullable(modelFetcher.apply(currentParent)).orElse(null);
        if (null == currentParentConfig) {
            return null;
        }
        if (!ModelTypeEnum.STORE.equals(currentParentConfig.getType())) {
            return null;
        }
        if (!StringUtils.isBlank(modelConfig.getMultiTable())
                && !StringUtils.isBlank(currentParentConfig.getMultiTableTypeField())) {
            return null;
        }
        return true;
    }

    /**
     * 递归判断
     *
     * @param model         子模型
     * @param judgeFunction 判断函数
     * @return 判断结果
     */
    default boolean recursion(String model,
                              BiFunction<String/*current model*/, String/*current parent*/, Boolean> judgeFunction) {
        return recursion(model, judgeFunction, null);
    }

    default boolean recursion(String model,
                              BiFunction<String/*current model*/, String/*current parent*/, Boolean> judgeFunction,
                              Function<String, ModelDefinition> modelFetcher) {
        if (null == modelFetcher) {
            modelFetcher = m -> Optional.ofNullable(PamirsSession.getContext())
                    .map(v -> v.getModelConfig(m)).map(ModelConfig::getModelDefinition).orElse(null);
        }
        ModelDefinition modelConfig = Optional.ofNullable(modelFetcher.apply(model)).orElse(null);
        if (null == modelConfig) {
            return false;
        }
        List<String> superModels = modelConfig.getSuperModels();
        if (!CollectionUtils.isEmpty(superModels)) {
            for (String superModel : superModels) {
                Boolean result = judgeFunction.apply(model, superModel);
                if (null == result) {
                    continue;
                }
                if (result) {
                    return true;
                }
                boolean isSuperTable = recursion(superModel, judgeFunction, modelFetcher);
                if (isSuperTable) {
                    return true;
                }
            }
        }
        return false;
    }

}
