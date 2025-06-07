package pro.shushi.pamirs.framework.compute;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.framework.compute.emnu.ComputeExpEnumerate;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 继承系统计算模板
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/19 2:16 上午
 */
public class InheritedComputeTemplate {

    private static final Object PRESENT = new Object();

    public static Result<Void> compute(Meta meta, ModelDefinition data, Map<String, Object> computeContext,
                                       BiFunction<ModelDefinition, List<String>, Result<Void>> listValidate,
                                       BiFunction<ModelDefinition, ModelDefinition, Result<Void>> singleValidate,
                                       boolean computeAfter,
                                       BiFunction<ModelDefinition, ModelDefinition, Boolean> continueInherited,
                                       BiConsumer<ModelDefinition, ModelDefinition> abstractInherited,
                                       BiConsumer<ModelDefinition, ModelDefinition> transientInherited,
                                       BiConsumer<ModelDefinition, ModelDefinition> multiTableInherited,
                                       BiConsumer<ModelDefinition, ModelDefinition> proxyInherited,
                                       BiConsumer<ModelDefinition, ModelDefinition> extendInherited,
                                       Consumer<ModelDefinition> currentConsumer) {
        return compute(data, meta::getModel,
                computeContext, listValidate, singleValidate, computeAfter, continueInherited,
                abstractInherited, transientInherited, multiTableInherited, proxyInherited, extendInherited,
                currentConsumer);
    }

    public static Result<Void> compute(ModelDefinition data, Function<String, ModelDefinition> modelFetcher, Map<String, Object> computeContext,
                                       BiFunction<ModelDefinition, List<String>, Result<Void>> listValidate,
                                       BiFunction<ModelDefinition, ModelDefinition, Result<Void>> singleValidate,
                                       boolean computeAfter,
                                       BiFunction<ModelDefinition, ModelDefinition, Boolean> continueInherited,
                                       BiConsumer<ModelDefinition, ModelDefinition> abstractInherited,
                                       BiConsumer<ModelDefinition, ModelDefinition> transientInherited,
                                       BiConsumer<ModelDefinition, ModelDefinition> multiTableInherited,
                                       BiConsumer<ModelDefinition, ModelDefinition> proxyInherited,
                                       BiConsumer<ModelDefinition, ModelDefinition> extendInherited,
                                       Consumer<ModelDefinition> currentConsumer) {
        Result<Void> result = new Result<>();
        if (null != computeContext) {
            if (computeContext.containsKey(data.getModel())) {
                return result;
            }
            computeContext.put(data.getModel(), PRESENT);
        }
        // 处理继承，不支持非jar包依赖模型继承
        List<String> inheritedList = data.getSuperModels();
        if (!CollectionUtils.isEmpty(inheritedList)) {
            if (null != listValidate) {
                result.fill(listValidate.apply(data, inheritedList));
                if (!result.isSuccess()) {
                    return result;
                }
            }
            for (int i = 0; i < inheritedList.size(); i++) {
                boolean isFirstSuperModel = i == 0;
                String inherited = inheritedList.get(i);
                // 递归处理父类
                ModelDefinition superModel = modelFetcher.apply(inherited);
                if (null == superModel) {
                    throw PamirsException.construct(ComputeExpEnumerate.BASE_MODULE_DEPENDENT_OR_MODEL_CONFIG_ERROR)
                            .appendMsg("model:" + data.getModel() + ", super model:" + inherited).errThrow();
                }
                if (null != singleValidate) {
                    result.fill(singleValidate.apply(data, superModel));
                    if (!result.isSuccess()) {
                        return result;
                    }
                }

                if (!computeAfter) {
                    computeInherited(data, superModel, isFirstSuperModel,
                            abstractInherited, transientInherited, multiTableInherited, proxyInherited, extendInherited);
                }
                if (null == continueInherited || continueInherited.apply(data, superModel)) {
                    Result<Void> inheritedResult = compute(superModel, modelFetcher, computeContext,
                            listValidate, singleValidate, computeAfter, continueInherited,
                            abstractInherited, transientInherited,
                            multiTableInherited, proxyInherited, extendInherited,
                            currentConsumer);
                    result.fill(inheritedResult);
                    if (!inheritedResult.isSuccess()) {
                        return result;
                    }
                }
                if (computeAfter) {
                    computeInherited(data, superModel, isFirstSuperModel,
                            abstractInherited, transientInherited, multiTableInherited, proxyInherited, extendInherited);
                }
            }
        } else {
            if (null != currentConsumer) {
                currentConsumer.accept(data);
            }
        }
        return result;
    }

    private static void computeInherited(ModelDefinition data, ModelDefinition superModel, boolean isFirstSuperModel,
                                         BiConsumer<ModelDefinition, ModelDefinition> abstractInherited,
                                         BiConsumer<ModelDefinition, ModelDefinition> transientInherited,
                                         BiConsumer<ModelDefinition, ModelDefinition> multiTableInherited,
                                         BiConsumer<ModelDefinition, ModelDefinition> proxyInherited,
                                         BiConsumer<ModelDefinition, ModelDefinition> extendInherited) {
        if (ModelTypeEnum.ABSTRACT.equals(superModel.getType())) {
            if (isFirstSuperModel) {
                // 首个父模型为Java直接继承的模型，需特殊处理继承方式
                // 用于支持数据管理器函数在无数据库表模型中使用
                // 其他模型需按原有继承逻辑执行
                if (ModelTypeEnum.PROXY.equals(data.getType())) {
                    if (null != proxyInherited) {
                        proxyInherited.accept(data, superModel);
                    }
                } else {
                    if (null != abstractInherited) {
                        abstractInherited.accept(data, superModel);
                    }
                }
            } else {
                if (null != abstractInherited) {
                    abstractInherited.accept(data, superModel);
                }
            }
        } else if (ModelTypeEnum.TRANSIENT.equals(superModel.getType()) || ModelTypeEnum.TRANSIENT.equals(data.getType())) {
            if (null != transientInherited) {
                transientInherited.accept(data, superModel);
            }
        } else if (!StringUtils.isBlank(data.getMultiTable()) && data.getMultiTable().equals(superModel.getModel())
                && ModelTypeEnum.STORE.equals(superModel.getType()) && ModelTypeEnum.STORE.equals(data.getType())) {
            if (null != multiTableInherited) {
                multiTableInherited.accept(data, superModel);
            }
        } else if (ModelTypeEnum.PROXY.equals(data.getType())) {
            if (null != proxyInherited) {
                proxyInherited.accept(data, superModel);
            }
        } else if (ModelTypeEnum.STORE.equals(superModel.getType())) {
            if (null != extendInherited) {
                extendInherited.accept(data, superModel);
            }
        }
    }

}
