package pro.shushi.pamirs.core.common.behavior.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.api.session.RequestContext;
import pro.shushi.pamirs.meta.base.AbstractModel;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author Adamancy Zhang
 * @date 2020-10-20 14:24
 */
public class SameTableBehavior {

    /**
     * not limit level.
     */
    private static final int DEFAULT_LEVEL = -1;

    /**
     * create data and parent data.
     *
     * @param data data
     * @param <T>  store model class
     */
    public static <T extends AbstractModel> void create(T data, String... modelModels) {
        createBatch(Collections.singletonList(data), modelModels);
    }

    /**
     * create data and parent data.
     *
     * @param data  data
     * @param level process level
     * @param <T>   store model class
     */
    public static <T extends AbstractModel> void create(T data, int level) {
        createBatch(Collections.singletonList(data), level);
    }

    /**
     * batch create data and parent data.
     *
     * @param data data list
     * @param <T>  store model class
     */
    public static <T extends AbstractModel> void createBatch(List<T> data, String... modelModels) {
        process(data, modelModels, list -> Models.data().createBatch(list));
    }

    /**
     * batch create data and parent data.
     *
     * @param data  data list
     * @param level process level
     * @param <T>   store model class
     */
    public static <T extends AbstractModel> void createBatch(List<T> data, int level) {
        process(data, level, list -> Models.data().createBatch(list));
    }

    /**
     * update data and parent data.
     *
     * @param data data
     * @param <T>  store model class
     */
    public static <T extends AbstractModel> void update(T data, String... modelModels) {
        updateBatch(Collections.singletonList(data), modelModels);
    }

    /**
     * update data and parent data.
     *
     * @param data  data
     * @param level process level
     * @param <T>   store model class
     */
    public static <T extends AbstractModel> void update(T data, int level) {
        updateBatch(Collections.singletonList(data), level);
    }

    /**
     * batch update data and parent data.
     *
     * @param data data list
     * @param <T>  store model class
     */
    public static <T extends AbstractModel> void updateBatch(List<T> data, String... modelModels) {
        process(data, modelModels, list -> Models.data().updateBatch(list));
    }

    /**
     * batch update data and parent data.
     *
     * @param data data list
     * @param <T>  store model class
     */
    public static <T extends AbstractModel> void updateBatch(List<T> data, int level) {
        process(data, level, list -> Models.data().updateBatch(list));
    }

    private static <T extends AbstractModel> void process(List<T> data, int limit, Consumer<List<T>> consumer) {
        process0(data, () -> getSuperStoreModel(data, limit), consumer);
    }

    private static <T extends AbstractModel> void process(List<T> data, String[] modelModels, Consumer<List<T>> consumer) {
        process0(data, () -> Arrays.asList(modelModels), consumer);
    }

    private static <T extends AbstractModel> void process0(List<T> data, Supplier<Collection<String>> modelModelsSupplier, Consumer<List<T>> consumer) {
        if (CollectionUtils.isEmpty(data)) {
            return;
        }
        consumer.accept(data);
        Collection<String> modelModels = modelModelsSupplier.get();
        for (String modelModel : modelModels) {
            for (T item : data) {
                copyPamirsData(item, modelModel);
            }
            consumer.accept(data);
        }
    }

    private static List<String> getSuperStoreModel(Object value, int limit) {
        RequestContext requestContext = PamirsSession.getContext();
        String modelModel = Models.api().getModel(value);
        ModelConfig modelConfig = requestContext.getModelConfig(modelModel);
        Set<String> tableMap = new HashSet<>();
        if (isNotSameTableModel(modelConfig, tableMap)) {
            return getSuperStoreModel(requestContext, modelConfig, 0, limit, limit <= DEFAULT_LEVEL, tableMap);
        }
        return new ArrayList<>();
    }

    private static List<String> getSuperStoreModel(RequestContext requestContext, ModelConfig modelConfig, int currentLevel, int limit, boolean isLimit, Set<String> tableMap) {
        List<String> superModels = modelConfig.getSuperModels();
        List<String> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(superModels)) {
            return result;
        }
        ModelConfig superModelConfig;
        for (String superModel : superModels) {
            superModelConfig = requestContext.getModelConfig(superModel);
            if (isNotSameTableModel(superModelConfig, tableMap)) {
                result.add(superModel);
            } else {
                if (isLimit) {
                    if (currentLevel < limit) {
                        result.addAll(getSuperStoreModel(requestContext, superModelConfig, currentLevel + 1, limit, Boolean.TRUE, tableMap));
                    }
                } else {
                    result.addAll(getSuperStoreModel(requestContext, superModelConfig, currentLevel + 1, limit, Boolean.FALSE, tableMap));
                }
            }
        }
        return result;
    }

    private static <T extends AbstractModel> void copyPamirsData(T data, String newModelModel) {
        Models.api().setModel(data, newModelModel);
        Models.api().setDataModel(newModelModel, data);
    }

    private static boolean isNotSameTableModel(ModelConfig modelConfig, Set<String> tableMap) {
        if (isStoreModel(modelConfig)) {
            String table = modelConfig.getTable();
            if (StringUtils.isNotBlank(table)) {
                int currentSize = tableMap.size();
                tableMap.add(table);
                return currentSize != tableMap.size();
            }
        }
        return Boolean.FALSE;
    }

    private static boolean isStoreModel(ModelConfig modelConfig) {
        ModelTypeEnum modelType = modelConfig.getType();
        if (ModelTypeEnum.STORE.equals(modelType) || ModelTypeEnum.PROXY.equals(modelType)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}
