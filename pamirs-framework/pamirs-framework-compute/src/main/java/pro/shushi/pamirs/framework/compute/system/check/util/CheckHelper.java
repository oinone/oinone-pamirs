package pro.shushi.pamirs.framework.compute.system.check.util;

import pro.shushi.pamirs.framework.compute.InheritedComputeTemplate;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.common.MetaBaseModel;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelField;

import java.util.*;
import java.util.function.Function;

/**
 * 校验帮助类
 * <p>
 * 2021/3/18 9:34 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class CheckHelper {

    public static <T extends MetaBaseModel> List<T> fetchModelChecker(String model, Function<String, List<T>> fetcher) {
        Map<String, T> checkerMap = new LinkedHashMap<>();
        ModelConfig modelConfig = PamirsSession.getContext().getModelConfig(model);
        InheritedComputeTemplate.compute(modelConfig.getModelDefinition(),
                v -> PamirsSession.getContext().getModelConfig(v).getModelDefinition(),
                new HashMap<>(),
                null, null, true, null,
                (currentModel, superModel) -> addModelCheckers(checkerMap, fetcher, currentModel.getModel()),
                (currentModel, superModel) -> addModelCheckers(checkerMap, fetcher, currentModel.getModel()),
                null,
                (currentModel, superModel) -> addModelCheckers(checkerMap, fetcher, currentModel.getModel()),
                (currentModel, superModel) -> addModelCheckers(checkerMap, fetcher, currentModel.getModel()),
                null);
        return new ArrayList<>(checkerMap.values());
    }

    public static <T extends MetaBaseModel> List<T> fetchModelFieldChecker(String model, String field, Function<String, List<T>> fetcher) {
        Map<String, T> checkerMap = new LinkedHashMap<>();
        ModelConfig modelConfig = PamirsSession.getContext().getModelConfig(model);
        InheritedComputeTemplate.compute(modelConfig.getModelDefinition(),
                v -> PamirsSession.getContext().getModelConfig(v).getModelDefinition(),
                new HashMap<>(),
                null, null, true, null,
                (currentModel, superModel) -> addModelFieldCheckers(checkerMap, fetcher, currentModel.getModel(), field),
                (currentModel, superModel) -> addModelFieldCheckers(checkerMap, fetcher, currentModel.getModel(), field),
                null,
                (currentModel, superModel) -> addModelFieldCheckers(checkerMap, fetcher, currentModel.getModel(), field),
                (currentModel, superModel) -> addModelFieldCheckers(checkerMap, fetcher, currentModel.getModel(), field),
                null);
        return new ArrayList<>(checkerMap.values());
    }

    public static <T extends MetaBaseModel> List<T> fetchFunctionChecker(String namespace, String fun, Function<String, List<T>> fetcher) {
        Map<String, T> checkerMap = new LinkedHashMap<>();
        ModelConfig modelConfig = PamirsSession.getContext().getModelConfig(namespace);
        if (null == modelConfig) {
            return new ArrayList<>(checkerMap.values());
        }
        InheritedComputeTemplate.compute(modelConfig.getModelDefinition(),
                v -> PamirsSession.getContext().getModelConfig(v).getModelDefinition(),
                new HashMap<>(),
                null, null, true, null,
                (currentModel, superModel) -> addFunctionCheckers(checkerMap, fetcher, currentModel.getModel(), fun),
                (currentModel, superModel) -> addFunctionCheckers(checkerMap, fetcher, currentModel.getModel(), fun),
                null,
                (currentModel, superModel) -> addFunctionCheckers(checkerMap, fetcher, currentModel.getModel(), fun),
                (currentModel, superModel) -> addFunctionCheckers(checkerMap, fetcher, currentModel.getModel(), fun),
                null);
        return new ArrayList<>(checkerMap.values());
    }

    private static <T extends MetaBaseModel> void addModelCheckers(Map<String, T> checkerMap, Function<String, List<T>> fetcher, String model) {
        List<T> additional = fetcher.apply(model);
        if (null != additional) {
            for(T t : additional){
                checkerMap.put(t.getSign(), t);
            }
        }
    }

    private static <T extends MetaBaseModel> void addModelFieldCheckers(Map<String, T> checkerMap, Function<String, List<T>> fetcher, String model, String field) {
        List<T> additional = fetcher.apply(ModelField.sign(model, field));
        if (null != additional) {
            for(T t : additional){
                checkerMap.put(t.getSign(), t);
            }
        }
    }

    private static <T extends MetaBaseModel> void addFunctionCheckers(Map<String, T> checkerMap, Function<String, List<T>> fetcher, String namespace, String fun) {
        List<T> additional = fetcher.apply(FunctionDefinition.sign(namespace, fun));
        if (null != additional) {
            for(T t : additional){
                checkerMap.put(t.getSign(), t);
            }
        }
    }

}
