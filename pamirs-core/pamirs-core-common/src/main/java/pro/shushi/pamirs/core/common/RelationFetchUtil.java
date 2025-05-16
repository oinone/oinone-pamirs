package pro.shushi.pamirs.core.common;

import org.apache.commons.collections4.CollectionUtils;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.base.IdRelation;
import pro.shushi.pamirs.meta.base.common.CodeRelation;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class RelationFetchUtil {

    public static final String LIMIT_1 = "LIMIT 1";

    private RelationFetchUtil() {
        //reject create object
    }

    public static <T extends IdRelation> T fetchOneById(String modelModel, Long id) {
        if (null == id) {
            return null;
        }
        return Models.data().queryOneByWrapper(Pops.<T>lambdaQuery().eq(T::getId, id)
                .setModel(modelModel));
    }

    public static <T extends IdRelation> List<T> fetchList(Collection<T> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return new ArrayList<>();
        }
        String modelModel = Models.api().getModel(dataList);
        List<Long> ids = new ArrayList<>();
        for (T item : dataList) {
            Long id = item.getId();
            if (id == null) {
                break;
            }
            ids.add(id);
        }
        return Models.data().queryListByWrapper(Pops.<T>lambdaQuery()
                .from(modelModel)
                .in(IdRelation::getId, ids));
    }

    public static <T extends IdRelation> List<T> fetchListByIds(String modelModel, Collection<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return new ArrayList<>();
        }
        return Models.data().queryListByWrapper(Pops.<T>lambdaQuery().in(T::getId, ids)
                .setModel(modelModel));
    }

    public static <T extends IdRelation> List<T> fetchListByIds(Class<T> tClass, Collection<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return new ArrayList<>();
        }
        return Models.data().queryListByWrapper(Pops.<T>lambdaQuery().in(T::getId, ids)
                .from(tClass));
    }

    public static <T extends IdRelation> Map<Long, T> fetchMapByIds(Class<T> tClass, Collection<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return new HashMap<>(1);
        }
        List<T> result = fetchListByIds(tClass, ids);
        if (CollectionUtils.isEmpty(result)) {
            return new HashMap<>(1);
        }
        return result.stream().collect(Collectors.toMap(T::getId, Function.identity()));
    }

    public static <T extends CodeRelation> Map<String, T> fetchMapByCodes(Class<T> tClass, Collection<String> codes) {
        if (CollectionUtils.isEmpty(codes)) {
            return new HashMap<>(1);
        }
        List<T> result = fetchListByCodes(tClass, codes);
        if (CollectionUtils.isEmpty(result)) {
            return new HashMap<>(1);
        }
        return result.stream().collect(Collectors.toMap(T::getCode, Function.identity()));
    }

    public static <T extends CodeRelation> List<T> fetchListByCodes(String modelModel, Collection<String> codes) {
        if (CollectionUtils.isEmpty(codes)) {
            return new ArrayList<>();
        }
        return Models.data().queryListByWrapper(Pops.<T>lambdaQuery().in(T::getCode, codes)
                .setModel(modelModel));
    }

    public static <T extends CodeRelation> List<T> fetchListByCodes(Class<T> tClass, Collection<String> codes) {
        if (CollectionUtils.isEmpty(codes)) {
            return new ArrayList<>();
        }
        List<String> distinctCodes = codes.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(distinctCodes)) {
            return new ArrayList<>();
        }
        return Models.data().queryListByWrapper(Pops.<T>lambdaQuery().in(T::getCode, distinctCodes)
                .from(tClass));
    }

}
