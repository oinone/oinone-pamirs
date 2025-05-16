package pro.shushi.pamirs.framework.connectors.data.mapper.provider;

import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.google.common.collect.Lists;
import org.apache.ibatis.builder.annotation.ProviderContext;
import org.apache.ibatis.builder.annotation.ProviderMethodResolver;
import pro.shushi.pamirs.framework.connectors.data.mapper.context.MapperContext;
import pro.shushi.pamirs.framework.connectors.data.mapper.method.util.SQLMethodUtils;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.api.session.PamirsSession;

import java.util.List;
import java.util.Objects;

/**
 * 通用mapper provider
 * <p>
 * 2020/6/28 10:33 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SuppressWarnings("unused")
public class PamirsMapperProvider extends AbstractMapperProvider implements ProviderMethodResolver {

    public static <T> String insert(T et, ProviderContext context) {
        String model = MapperContext.modelFromMapper(context.getMapperType(), et);
        ModelConfig modelConfig = fetchModelConfig(model);
        return fetch(modelConfig, "insert", key -> insert(modelConfig, false));
    }

    public static <T> String insertBatchForeach(List<T> coll, ProviderContext context) {
        String model = MapperContext.modelFromMapper(context.getMapperType(), coll);
        ModelConfig modelConfig = fetchModelConfig(model);
        return fetch(modelConfig, "insertBatchForeach", key -> insertBatch(modelConfig, false));
    }

    public static <T> String insertOrUpdate(T et, ProviderContext context) {
        Models.check().checkPkOrUniqueKeyValueValid(MapperContext.objFromMapper(et, Constants.ENTITY));
        String model = MapperContext.modelFromMapper(context.getMapperType(), et);
        ModelConfig modelConfig = fetchModelConfig(model);
        return fetch(modelConfig, "insertOrUpdate", key -> insert(modelConfig, true));
    }

    public static <T> String insertOrUpdateBatchOnDuplicateKey(List<T> coll, ProviderContext context) {
        Models.check().checkListPkOrUniqueKeyValueValid(coll);
        String model = MapperContext.modelFromMapper(context.getMapperType(), coll);
        ModelConfig modelConfig = fetchModelConfig(model);
        return fetch(modelConfig, "insertOrUpdateBatchOnDuplicateKey", key -> insertBatch(modelConfig, true));
    }

    public static <T> String updateByUniqueKey(T et, ProviderContext context) {
        Object entity = MapperContext.objFromMapper(et, Constants.ENTITY);
        Models.check().checkPkOrUniqueKeyValueValid(entity);
        String model = MapperContext.modelFromMapper(context.getMapperType(), et);
        ModelConfig modelConfig = fetchModelConfig(model);
        String[] nonEmptyUniqueKey = Models.compute().findFirstValidUniqueKey(Lists.newArrayList(entity));
        return fetch(modelConfig, "updateByUniqueKey", key -> {
            boolean optimisticLocker = Objects.requireNonNull(PamirsSession.directive()).isOptimisticLocker();
            return updateByUniqueKey(modelConfig, false, false, optimisticLocker, nonEmptyUniqueKey);
        }, nonEmptyUniqueKey);
    }

    public static <T> String updateBatchForeachByUniqueKey(List<T> coll, ProviderContext context) {
        Models.check().checkListPkOrUniqueKeyValueValid(coll);
        String model = MapperContext.modelFromMapper(context.getMapperType(), coll);
        ModelConfig modelConfig = fetchModelConfig(model);
        String[] nonEmptyUniqueKey = Models.compute().findFirstValidUniqueKey(coll);
        return fetch(modelConfig, "updateBatchForeachByUniqueKey", key -> {
            boolean optimisticLocker = Objects.requireNonNull(PamirsSession.directive()).isOptimisticLocker();
            return updateByUniqueKey(modelConfig, true, true, optimisticLocker, nonEmptyUniqueKey);
        }, nonEmptyUniqueKey);
    }

    public static <T> String updateByPk(final T et, ProviderContext context) {
        Object entity = MapperContext.objFromMapper(et, Constants.ENTITY);
        Models.check().checkPkValueValid(entity);
        String model = MapperContext.modelFromMapper(context.getMapperType(), et);
        ModelConfig modelConfig = fetchModelConfig(model);
        return fetch(modelConfig, "updateByPk", key -> {
            boolean optimisticLocker = Objects.requireNonNull(PamirsSession.directive()).isOptimisticLocker();
            return updateByPk(modelConfig, true, false, optimisticLocker);
        });
    }

    public static <T> String updateByPks(T et, final List<T> cc, ProviderContext context) {
        Models.check().checkListPkValueValid(cc);
        String model = MapperContext.modelFromMapper(context.getMapperType(), et, cc);
        ModelConfig modelConfig = fetchModelConfig(model);
        return fetch(modelConfig, "updateByPks", key -> {
            boolean optimisticLocker = Objects.requireNonNull(PamirsSession.directive()).isOptimisticLocker();
            return updateByPks(modelConfig, true, false, false, optimisticLocker);
        });
    }

    public static <T> String update(T et, final IWrapper<T> ew, ProviderContext context) {
        String model = MapperContext.modelFromMapper(context.getMapperType(), et, ew);
        ModelConfig modelConfig = fetchModelConfig(model);
        return fetch(modelConfig, "update", key -> {
            boolean optimisticLocker = Objects.requireNonNull(PamirsSession.directive()).isOptimisticLocker();
            return update(modelConfig, true, true, true, false, false);
        });
    }

    public static <T> String deleteByPk(final T et, ProviderContext context) {
        Models.check().checkPkValueValid(MapperContext.objFromMapper(et, Constants.ENTITY));
        String model = MapperContext.modelFromMapper(context.getMapperType(), et);
        ModelConfig modelConfig = fetchModelConfig(model);
        return fetch(modelConfig, "deleteByPk", key -> deleteByPk(modelConfig));
    }

    public static <T> String deleteByPks(final List<T> cc, ProviderContext context) {
        Models.check().checkListPkValueValid(cc);
        String model = MapperContext.modelFromMapper(context.getMapperType(), cc);
        ModelConfig modelConfig = fetchModelConfig(model);
        return fetch(modelConfig, "deleteByPks", key -> deleteByPks(modelConfig));
    }

    public static <T> String deleteByEntity(final T et, ProviderContext context) {
        String model = MapperContext.modelFromMapper(context.getMapperType(), et);
        ModelConfig modelConfig = fetchModelConfig(model);
        return fetch(modelConfig, "deleteByEntity", key -> delete(modelConfig, false));
    }

    public static <T> String delete(final IWrapper<T> ew, ProviderContext context) {
        String model = MapperContext.modelFromMapper(context.getMapperType(), ew);
        ModelConfig modelConfig = fetchModelConfig(model);
        return fetch(modelConfig, "delete", key -> delete(modelConfig, true));
    }

    public static <T> String deleteByUniqueKey(final T et, ProviderContext context) {
        Object entity = MapperContext.objFromMapper(et, Constants.ENTITY);
        Models.check().checkPkOrUniqueKeyValueValid(entity);
        String model = MapperContext.modelFromMapper(context.getMapperType(), et);
        ModelConfig modelConfig = fetchModelConfig(model);
        String[] nonEmptyUniqueKey = Models.compute().findFirstValidUniqueKey(Lists.newArrayList(entity));
        return fetch(modelConfig, "deleteByUniqueKey", key -> deleteByUniqueKey(modelConfig, nonEmptyUniqueKey), nonEmptyUniqueKey);
    }

    public static <T> String deleteByUniqueKeys(final List<T> cc, ProviderContext context) {
        Models.check().checkListPkOrUniqueKeyValueValid(cc);
        String model = MapperContext.modelFromMapper(context.getMapperType(), cc);
        ModelConfig modelConfig = fetchModelConfig(model);
        String[] nonEmptyUniqueKey = Models.compute().findFirstValidUniqueKey(cc);
        return fetch(modelConfig, "deleteByUniqueKeys", key -> deleteByUniqueKeys(modelConfig, nonEmptyUniqueKey), nonEmptyUniqueKey);
    }

    public static <T> String selectByPk(final T pk, ProviderContext context) {
        Models.check().checkPkValueValid(MapperContext.objFromMapper(pk, Constants.ENTITY));
        String model = MapperContext.modelFromMapper(context.getMapperType(), pk);
        ModelConfig modelConfig = fetchModelConfig(model);
        return fetch(modelConfig, "selectByPk", key -> selectByPk(modelConfig, false, false, true));
    }

    public static <T> String selectOne(final IWrapper<T> ew, ProviderContext context) {
        String model = MapperContext.modelFromMapper(context.getMapperType(), ew);
        ModelConfig modelConfig = fetchModelConfig(model);
        return fetch(modelConfig, "selectOne", key -> select(modelConfig, false, true, true,
                SQLMethodUtils.isWrapperEntityMap(ew)));
    }

    public static <T> String selectOneByEntity(final T et, ProviderContext context) {
        Object entity = MapperContext.objFromMapper(et, Constants.ENTITY);
        String model = MapperContext.modelFromMapper(context.getMapperType(), et);
        ModelConfig modelConfig = fetchModelConfig(model);
        return fetch(modelConfig, "selectOneByEntity", key -> select(modelConfig, false, false, true,
                SQLMethodUtils.isEntityMap(entity)));
    }

    public static <T> String selectOneByUniqueKey(final T et, ProviderContext context) {
        Object entity = MapperContext.objFromMapper(et, Constants.ENTITY);
        Models.check().checkUniqueKeyValueValid(entity);
        String model = MapperContext.modelFromMapper(context.getMapperType(), et);
        ModelConfig modelConfig = fetchModelConfig(model);
        String[] nonEmptyUniqueKey = Models.compute().findFirstValidUniqueKey(Lists.newArrayList(entity));
        return fetch(modelConfig, "selectOneByUniqueKey",
                key -> selectByUnique(modelConfig, false, false, true, nonEmptyUniqueKey), nonEmptyUniqueKey);
    }

    public static <T> String selectListByPks(final List<T> cc, ProviderContext context) {
        Models.check().checkListPkValueValid(cc);
        String model = MapperContext.modelFromMapper(context.getMapperType(), cc);
        ModelConfig modelConfig = fetchModelConfig(model);
        return fetch(modelConfig, "selectListByPks", key -> selectByPks(modelConfig, false, false, true));
    }

    public static <T> String selectListByUniqueKey(final List<T> cc, ProviderContext context) {
        Models.check().checkListPkOrUniqueKeyValueValid(cc);
        String model = MapperContext.modelFromMapper(context.getMapperType(), cc);
        ModelConfig modelConfig = fetchModelConfig(model);
        String[] nonEmptyUniqueKey = Models.compute().findFirstValidUniqueKey(cc);
        return fetch(modelConfig, "selectListByUniqueKey",
                key -> selectByUniques(modelConfig, false, false, true, nonEmptyUniqueKey), nonEmptyUniqueKey);
    }

    public static <T> String selectListByEntity(final T et, ProviderContext context) {
        Object entity = MapperContext.objFromMapper(et, Constants.ENTITY);
        String model = MapperContext.modelFromMapper(context.getMapperType(), et);
        ModelConfig modelConfig = fetchModelConfig(model);
        return fetch(modelConfig, "selectListByEntity", key -> select(modelConfig, false, false, true,
                SQLMethodUtils.isEntityMap(entity)));
    }

    public static <T> String selectCountByEntity(final T et, ProviderContext context) {
        Object entity = MapperContext.objFromMapper(et, Constants.ENTITY);
        String model = MapperContext.modelFromMapper(context.getMapperType(), et);
        ModelConfig modelConfig = fetchModelConfig(model);
        return fetch(modelConfig, "selectCountByEntity", key -> selectCount(modelConfig, false, true,
                SQLMethodUtils.isEntityMap(entity)));
    }

    public static <T> String selectCount(final IWrapper<T> ew, ProviderContext context) {
        String model = MapperContext.modelFromMapper(context.getMapperType(), ew);
        ModelConfig modelConfig = fetchModelConfig(model);
        return fetch(modelConfig, "selectCount", key -> selectCount(modelConfig, true, true,
                SQLMethodUtils.isWrapperEntityMap(ew)));
    }

    public static <T> String selectList(final IWrapper<T> ew, ProviderContext context) {
        String model = MapperContext.modelFromMapper(context.getMapperType(), ew);
        ModelConfig modelConfig = fetchModelConfig(model);
        return fetch(modelConfig, "selectList", key -> select(modelConfig, false, true, true,
                SQLMethodUtils.isWrapperEntityMap(ew)));
    }

    public static <T, E extends Pagination<T>> String selectListByPage(final E page, final IWrapper<T> ew, ProviderContext context) {
        String model = MapperContext.modelFromMapper(context.getMapperType(), page, ew);
        ModelConfig modelConfig = fetchModelConfig(model);
        return fetch(modelConfig, "selectListByPage", key -> select(modelConfig, false, true, true,
                SQLMethodUtils.isWrapperEntityMap(ew)));
    }

    private static ModelConfig fetchModelConfig(String model) {
        return Objects.requireNonNull(PamirsSession.getContext()).getModelConfig(model);
    }

}
