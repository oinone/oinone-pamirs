package pro.shushi.pamirs.framework.connectors.data.util;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import pro.shushi.pamirs.framework.connectors.data.dialect.holder.SQLBatchExecuteDialectServiceHolder;
import pro.shushi.pamirs.framework.connectors.data.enmu.DataExpEnumerate;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.enmu.BatchCommitTypeEnum;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.util.ListUtils;
import pro.shushi.pamirs.meta.constant.FieldConstants;

import javax.validation.constraints.NotNull;
import java.text.MessageFormat;
import java.util.*;
import java.util.function.Function;

import static pro.shushi.pamirs.framework.connectors.data.enmu.DataExpEnumerate.BASE_BATCH_EXECUTE_WITH_SIZE_ERROR;

/**
 * 批量工具类
 * 2020/12/15 7:06 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class MapperUtils {

    public static <T> int batchFunction(@NotNull List<T> entityList, Function<List<T>, Integer> function, int batchSize) {
        if (CollectionUtils.isEmpty(entityList)) {
            return 0;
        }
        int result = 0;
        int size = entityList.size();
        if (batchSize < 0 || size <= batchSize) {
            return applyFunctionForBatch(function, result, entityList);
        }
        List<T> subEntityList;
        for (int from = 0, to; from < size; from = to) {
            to = Math.min(from + batchSize, size);
            subEntityList = ListUtils.sub(entityList, from, to);
            // 进行分批处理
            result = applyFunctionForBatch(function, result, subEntityList);
        }
        return result;
    }

    private static <T> int applyFunctionForBatch(Function<List<T>, Integer> function, int result, List<T> entityListPer) {
        String dsKey = DataConfigurationHelper.getDsKey();
        int count = SQLBatchExecuteDialectServiceHolder.get(dsKey).batchSubmit(function, entityListPer);
        BatchCommitTypeEnum batchOperationEnum = PamirsSession.getBatchOperation();
        if (BatchCommitTypeEnum.useAndJudgeAffectRows.equals(batchOperationEnum) && entityListPer.size() != count) {
            throw PamirsException.construct(BASE_BATCH_EXECUTE_WITH_SIZE_ERROR).errThrow();
        }
        result += count;
        return result;
    }

    public static <T> void insertOrUpdatePartition(List<T> entityList, List<T> queryFromDbList,
                                                   List<T> insertList, List<T> updateList,
                                                   ModelConfig modelConfig, Function<T, String> uniqueKeyValueGenerator) {
        Map<String/*uniqueKey*/, T> existItemMap = new HashMap<>();
        for (T existItem : queryFromDbList) {
            String uniqueKeyValue = uniqueKeyValueGenerator.apply(existItem);
            existItemMap.put(uniqueKeyValue, existItem);
        }
        for (T entity : entityList) {
            String uniqueKeyValue = uniqueKeyValueGenerator.apply(entity);
            if (existItemMap.containsKey(uniqueKeyValue)) {
                T existItem = existItemMap.get(uniqueKeyValue);
                Models.compute().setPkIfPresent(modelConfig, entity, existItem);
                Models.compute().setCodeIfPresent(modelConfig, entity, existItem);
                updateList.add(entity);
            } else {
                insertList.add(entity);
            }
        }
    }

    public static <T> int insertOrUpdateBatch(List<T> entityList,
                                              Function<List<T>, List<T>> selectListByPks,
                                              Function<List<T>, List<T>> selectListByUniqueKey,
                                              Function<List<T>, Integer> insertBatch,
                                              Function<List<T>, Integer> updateBatchByUniqueKey) {
        if (CollectionUtils.isEmpty(entityList)) {
            return 0;
        }
        List<T> pkdList = new ArrayList<>();
        List<T> nonPkdList = new ArrayList<>();
        for (T entity : entityList) {
            if (Models.compute().isPkValueValid(entity)) {
                pkdList.add(entity);
            } else {
                nonPkdList.add(entity);
            }
        }
        int result = 0;
        String model = Models.api().getModel(entityList);
        ModelConfig modelConfig = Optional.ofNullable(PamirsSession.getContext())
                .map(v -> v.getModelConfig(model)).orElse(null);
        List<T> insertList = new ArrayList<>();
        List<T> updateByPkList = null;
        List<T> updateByUniqueList = null;
        if (CollectionUtils.isNotEmpty(pkdList)) {
            updateByPkList = new ArrayList<>();
            List<T> queryFromDbList = selectListByPks.apply(pkdList);
            MapperUtils.insertOrUpdatePartition(pkdList, queryFromDbList, insertList, updateByPkList, modelConfig,
                    entity -> Models.compute().generateValidPksValue(model, entity));
        }
        if (CollectionUtils.isNotEmpty(nonPkdList)) {
            String[] nonEmptyUniqueKey = Models.compute().findFirstValidUniqueKey(nonPkdList);
            if (ArrayUtils.isNotEmpty(nonEmptyUniqueKey)) {
                updateByUniqueList = new ArrayList<>();
                List<T> queryFromDbList = selectListByUniqueKey.apply(nonPkdList);
                MapperUtils.insertOrUpdatePartition(nonPkdList, queryFromDbList, insertList, updateByUniqueList, modelConfig,
                        entity -> Models.compute().generateValidUniqueKeyValue(nonEmptyUniqueKey, entity));
            } else {
                Set<String> allUniqueKeys = Models.compute().fetchAllUniqueKeys(modelConfig);
                if (CollectionUtils.isEmpty(allUniqueKeys) || allUniqueKeys.contains(FieldConstants.CODE)) {
                    insertList.addAll(nonPkdList);
                } else {
                    throw PamirsException.construct(DataExpEnumerate.BASE_CREATE_OR_UPDATE_DATA_ERROR)
                            .appendMsg(MessageFormat.format("model:{0}", model)).errThrow();
                }
            }
        }
        if (CollectionUtils.isNotEmpty(insertList)) {
            result += insertBatch.apply(insertList);
        }
        if (CollectionUtils.isNotEmpty(updateByPkList)) {
            result += updateBatchByUniqueKey.apply(updateByPkList);
        }
        if (CollectionUtils.isNotEmpty(updateByUniqueList)) {
            result += updateBatchByUniqueKey.apply(updateByUniqueList);
        }
        return result;
    }

}
