package pro.shushi.pamirs.file.api.extpoint;

import org.apache.commons.collections4.CollectionUtils;
import pro.shushi.pamirs.file.api.context.ExcelImportContext;
import pro.shushi.pamirs.file.api.entity.EasyExcelBlockDefinition;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.orm.systems.directive.SystemDirectiveEnum;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.D;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Adamancy Zhang on 2021-04-14 20:20
 */
public abstract class AbstractExcelImportM2mDataExtPointImpl<T> implements ExcelImportM2mDataExtPoint<T> {

    @Override
    public List<T> importM2mData(ExcelImportContext importContext, List<T> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return dataList;
        }
        EasyExcelBlockDefinition blockDefinition = importContext.getDefinitionContext().getSheetList().get(0).getBlockDefinitions().get(0);
        ModelConfig modelConfig = PamirsSession.getContext().getModelConfig(blockDefinition.getBindingModel());

        // 通过模型和excel定义对比,找到一个唯一键,用于查询数据
        List<String> searchFields = matchSearchFields(blockDefinition.getFieldCells().keySet(), modelConfig);
        if (CollectionUtils.isEmpty(searchFields)) {
            // 没有唯一键,不做查询,直接返回
            return dataList;
        }

        List<Map<String, Object>> excelDataMapList = dataList.stream().map(obj -> convertDataMap.apply(obj)).collect(Collectors.toList());
        QueryWrapper<Object> queryWrapper = Pops.query().from(modelConfig.getModel());
        queryWrapper.in(
                searchFields,
                searchFields.stream().map(_f -> excelDataMapList.stream().map(_d -> _d.get(_f)).collect(Collectors.toList())).toArray(List[]::new)
        );

        List<Object> dbDataList = Models.directive().run(() -> Models.data().queryListByWrapper(queryWrapper),
                SystemDirectiveEnum.EXT_POINT,
                SystemDirectiveEnum.FROM_CLIENT,
                SystemDirectiveEnum.BUILT_ACTION);
        if (CollectionUtils.isEmpty(dbDataList)) {
            // 查不到数据,返回空
            return new ArrayList<>();
        }

        List<Map<String, Object>> dbDataMapList = dbDataList.stream().map(obj -> convertDataMap.apply(obj)).collect(Collectors.toList());

        Map<String, Map<String, Object>> unique2DbData = dbDataMapList.stream().collect(Collectors.toMap(
                _d -> convertUniqueValue.apply(_d, searchFields), i -> i
        ));

        List<T> result = new ArrayList<>();
        // 将db数据合并到excel数据做覆盖. excel数据可以大于db数据
        for (T t : dataList) {
            Map<String, Object> excelData = convertDataMap.apply(t);
            Map<String, Object> dbData = unique2DbData.get(
                    convertUniqueValue.apply(excelData, searchFields)
            );
            if (dbData == null) {
                // 忽略
                continue;
            }
            // db能查到的值,全量覆盖
            excelData.putAll(dbData);

            result.add(t);
        }
        return result;
    }

    /**
     * 对象map,根据字段列表,构造唯一键
     */
    private BiFunction<Map<String, Object>, List<String>, String> convertUniqueValue = (_data, _fields) ->
            _fields.stream()
                    .map(_data::get)
                    .map(String::valueOf)
                    .collect(Collectors.joining("-"));

    /**
     * 对象转map
     */
    private Function<Object, Map<String, Object>> convertDataMap = _data -> {
        if (_data instanceof D) {
            return ((D) _data).get_d();
        }
        return (Map<String, Object>) _data;
    };

    private List<String> matchSearchFields(Set<String> excelFields, ModelConfig modelConfig) {
        List<String> result;

        result = matchSearchFields0(excelFields, modelConfig.getPk());
        if (CollectionUtils.isNotEmpty(result)) {
            return result;
        }

        if (CollectionUtils.isNotEmpty(modelConfig.getUniques())) {
            for (String unique : modelConfig.getUniques()) {
                result = matchSearchFields0(excelFields, Arrays.stream(unique.split(CharacterConstants.SEPARATOR_COMMA)).map(String::trim).collect(Collectors.toList()));
                if (CollectionUtils.isNotEmpty(result)) {
                    return result;
                }
            }
        }
        return result;
    }

    private List<String> matchSearchFields0(Set<String> excelFields, List<String> unique) {
        if (CollectionUtils.isEmpty(unique)) {
            return null;
        }
        for (String s : unique) {
            if (!excelFields.contains(s)) {
                return null;
            }
        }
        return unique;
    }
}
