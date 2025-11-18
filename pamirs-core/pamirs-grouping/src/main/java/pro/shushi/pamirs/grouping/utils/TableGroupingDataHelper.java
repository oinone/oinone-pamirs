package pro.shushi.pamirs.grouping.utils;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.core.common.tmodel.CommonConditionWrapper;
import pro.shushi.pamirs.framework.common.entry.NullValue;
import pro.shushi.pamirs.framework.orm.json.PamirsDataUtils;
import pro.shushi.pamirs.grouping.entity.GroupingDataWrapper;
import pro.shushi.pamirs.grouping.entity.TableGroupingFieldQuery;
import pro.shushi.pamirs.grouping.model.GroupingData;
import pro.shushi.pamirs.grouping.model.GroupingField;
import pro.shushi.pamirs.grouping.model.TableGroupingWrapper;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.enmu.IEnum;
import pro.shushi.pamirs.meta.enmu.SortDirectionEnum;
import pro.shushi.pamirs.meta.util.FieldUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 表格分组数据帮助类
 *
 * @author Adamancy Zhang at 14:18 on 2025-11-13
 */
@Slf4j
public class TableGroupingDataHelper {

    private static final String NULL_VALUE = "__null__";

    private TableGroupingDataHelper() {
        // reject create object
    }

    public static List<TableGroupingFieldQuery> prepareGroupingFields(TableGroupingWrapper wrapper) {
        CommonConditionWrapper queryWrapper = wrapper.getQueryWrapper();
        List<TableGroupingFieldQuery> queryList = new ArrayList<>();
        List<GroupingField> fields = wrapper.getFields();
        if (fields.size() == 1) {
            queryList.add(new TableGroupingFieldQuery(queryWrapper, fields.get(0), wrapper.getStatisticField()));
        } else {
            TableGroupingFieldQuery parent = null;
            int lastIndex = fields.size() - 1;
            for (int i = 0; i < lastIndex; i++) {
                GroupingField field = fields.get(i);
                parent = new TableGroupingFieldQuery(queryWrapper, field, parent);
                queryList.add(parent);
            }
            queryList.add(new TableGroupingFieldQuery(queryWrapper, fields.get(lastIndex), wrapper.getStatisticField(), parent));
        }
        return queryList;
    }

    public static Map<String, GroupingDataWrapper> generatorGroupingDataList(List<TableGroupingFieldQuery> queryList, List<?> list) {
        Map<String, GroupingDataWrapper> groupingDataMap = new LinkedHashMap<>();
        generatorGroupingDataList(groupingDataMap, queryList, list, true);
        return groupingDataMap;
    }

    public static void generatorGroupingDataList(Map<String, GroupingDataWrapper> groupingDataMap, List<TableGroupingFieldQuery> queryList, List<?> list, boolean addData) {
        for (Object data : list) {
            int lastIndex = queryList.size() - 1;
            GroupingDataWrapper lastWrapper = null;
            for (int i = 0; i < lastIndex; i++) {
                TableGroupingFieldQuery query = queryList.get(i);
                lastWrapper = computeIfAbsent(groupingDataMap, query, data, false);
            }
            if (lastWrapper == null) {
                lastWrapper = computeIfAbsent(groupingDataMap, queryList.get(lastIndex), data, true);
            } else {
                lastWrapper = computeIfAbsent(lastWrapper.getGroupings(), queryList.get(lastIndex), data, true);
            }
            if (addData) {
                lastWrapper.addData(data);
            }
        }
    }

    public static GroupingDataWrapper generatorGroupingDataWrapper(TableGroupingFieldQuery query, Object data, boolean isLeaf) {
        Object value = getGroupValue(query, data);
        String key = getGroupKeyByValue(query, value);
        Object serializeValue = serializeGroupValue(query, value);
        return new GroupingDataWrapper(query, key, generatorGroupingData(query.getField(), serializeValue, isLeaf), value);
    }

    public static GroupingDataWrapper computeIfAbsent(Map<String, GroupingDataWrapper> groupingDataMap, TableGroupingFieldQuery query, Object data, boolean isLeaf) {
        Object value = getGroupValue(query, data);
        Object serializeValue = serializeGroupValue(query, value);
        return groupingDataMap.computeIfAbsent(getGroupKeyByValue(query, value),
                key -> new GroupingDataWrapper(query, key, generatorGroupingData(query.getField(), serializeValue, isLeaf), value));
    }

    private static GroupingData generatorGroupingData(String field, Object value, Boolean isLeaf) {
        GroupingData groupingData = new GroupingData();
        groupingData.setField(field);
        groupingData.setValue(value);
        groupingData.setIsLeaf(isLeaf);
        return groupingData;
    }

    public static Map<String, GroupingDataWrapper> mergeGroupingDataList(Map<String, GroupingDataWrapper> lastGroupingData, List<GroupingDataWrapper> groupingData) {
        Map<String, GroupingDataWrapper> nextGroupingData = new LinkedHashMap<>();
        for (GroupingDataWrapper groupingDataWrapper : groupingData) {
            String parentKey = groupingDataWrapper.getParentKey();
            GroupingDataWrapper wrapper = lastGroupingData.get(parentKey);
            if (wrapper == null) {
                if (NULL_VALUE.equals(parentKey)) {
                    wrapper = new GroupingDataWrapper(null, parentKey, generatorGroupingData(groupingDataWrapper.getParentField(), NullValue.INSTANCE, false), NullValue.INSTANCE);
                    lastGroupingData.put(parentKey, wrapper);
                } else {
//                    log.error("Invalid parent grouping data wrapper. field: {}, key: {}, parentKey: {}", groupingDataWrapper.getField(), groupingDataWrapper.getKey(), parentKey);
                    continue;
                }
            }
            String key = getGroupKeyByValue(groupingDataWrapper.getQuery(), groupingDataWrapper.getValue());
            wrapper.getGroupings().put(key, groupingDataWrapper);
            nextGroupingData.put(key, groupingDataWrapper);
        }
        return nextGroupingData;
    }

    public static List<GroupingData> collectionGroupingData(String model, Map<String, GroupingDataWrapper> groupingDataMap, List<TableGroupingFieldQuery> queryList) {
        return collectionGroupingData(model, groupingDataMap, queryList, 0);
    }

    private static List<GroupingData> collectionGroupingData(String model, Map<String, GroupingDataWrapper> groupingDataMap, List<TableGroupingFieldQuery> queryList, int queryIndex) {
        List<GroupingData> results = new ArrayList<>();
        TableGroupingFieldQuery query = queryList.get(queryIndex);
        List<GroupingDataWrapper> sortedGroupingDataWrappers = sortGroupingDataWrappers(query, groupingDataMap.values());
        for (GroupingDataWrapper wrapper : sortedGroupingDataWrappers) {
            GroupingData groupingData = wrapper.getData();
            if (groupingData.getIsLeaf()) {
                List<Object> data = wrapper.getResults();
                if (CollectionUtils.isNotEmpty(data)) {
                    groupingData.setData(PamirsDataUtils.toJSONString(model, data));
                }
            } else {
                Map<String, GroupingDataWrapper> nextGroupingDataMap = wrapper.getGroupings();
                if (!nextGroupingDataMap.isEmpty()) {
                    groupingData.setGroups(collectionGroupingData(model, nextGroupingDataMap, queryList, queryIndex + 1));
                }
            }
            results.add(groupingData);
        }
        return results;
    }

    private static List<GroupingDataWrapper> sortGroupingDataWrappers(TableGroupingFieldQuery query, Collection<GroupingDataWrapper> values) {
        SortDirectionEnum direction = query.getDirection();
        switch (direction) {
            case ASC:
                return values.stream().sorted((a, b) -> {
                    String ak = a.getKey();
                    String bk = b.getKey();
                    if (NULL_VALUE.equals(ak)) {
                        return 1;
                    }
                    if (NULL_VALUE.equals(bk)) {
                        return -1;
                    }
                    return ak.compareTo(bk);
                }).collect(Collectors.toList());
            case DESC:
                return values.stream().sorted((a, b) -> {
                    String ak = a.getKey();
                    String bk = b.getKey();
                    if (NULL_VALUE.equals(ak)) {
                        return 1;
                    }
                    if (NULL_VALUE.equals(bk)) {
                        return -1;
                    }
                    return bk.compareTo(ak);
                }).collect(Collectors.toList());
            default:
                throw new UnsupportedOperationException("Invalid sort direction enumeration.");
        }
    }

    public static String getGroupKeyByData(TableGroupingFieldQuery query, Object data) {
        Object value = getGroupValue(query, data);
        return getGroupKeyByValue(query, value);
    }

    private static String getGroupKeyByValue(TableGroupingFieldQuery query, Object value) {
        if (NullValue.INSTANCE.equals(value)) {
            return NULL_VALUE;
        }
        if (query.isMulti()) {
            Collection<?> coll = (Collection<?>) value;
            if (query.isEnumField()) {
                return coll.stream().map(v -> convertEnumerationValue(query, v)).map(String::valueOf).collect(Collectors.joining(CharacterConstants.SEPARATOR_COMMA));
            }
        }
        return String.valueOf(value);
    }

    public static String getGroupKeyByClientValue(TableGroupingFieldQuery query, Object value) {
        if (value == null || NullValue.INSTANCE.equals(value)) {
            return NULL_VALUE;
        }
        if (query.isMulti()) {
            Collection<?> coll = (Collection<?>) value;
            if (coll.isEmpty()) {
                return NULL_VALUE;
            }
            if (query.isEnumField()) {
                return coll.stream().map(v -> convertEnumerationValue(query, v)).map(String::valueOf).collect(Collectors.joining(CharacterConstants.SEPARATOR_COMMA));
            }
        }
        return String.valueOf(value);
    }

    @SuppressWarnings("DataFlowIssue")
    private static Object getGroupValue(TableGroupingFieldQuery query, Object data) {
        if (data == null) {
            return NullValue.INSTANCE;
        }
        String field = query.getField();
        Object value = FieldUtils.getFieldValue(data, field);
        if (value == null) {
            return NullValue.INSTANCE;
        }
        if (value instanceof String && StringUtils.isBlank((String) value)) {
            return NullValue.INSTANCE;
        }
        boolean isChanged = false;
        if (query.isMulti()) {
            Collection<?> coll = (Collection<?>) value;
            if (coll.isEmpty()) {
                return NullValue.INSTANCE;
            }
            if (query.isNumberField()) {
                value = coll.stream().sorted().collect(Collectors.toList());
            } else if (query.isEnumField()) {
                value = coll.stream()
                        .sorted(Comparator.comparing(v -> convertEnumerationValue(query, v)))
                        .collect(Collectors.toList());
            } else {
                value = coll.stream()
                        .sorted(Comparator.comparing(String::valueOf))
                        .collect(Collectors.toList());
            }
            isChanged = true;
        }
        if (isChanged) {
            FieldUtils.setFieldValue(data, field, value);
        }
        return value;
    }

    private static Object serializeGroupValue(TableGroupingFieldQuery query, Object value) {
        if (NullValue.INSTANCE.equals(value)) {
            return NullValue.INSTANCE;
        }
        if (query.isMulti()) {
            Collection<?> coll = (Collection<?>) value;
            if (query.isEnumField()) {
                return coll.stream()
                        .map(TableGroupingDataHelper::convertEnumerationName)
                        .collect(Collectors.toList());
            }
        } else if (query.isEnumField()) {
            return convertEnumerationName(value);
        }
        return value;
    }

    private static String convertEnumerationName(Object value) {
        String name;
        if (value instanceof IEnum) {
            name = ((IEnum<?>) value).name();
        } else if (value instanceof String) {
            name = (String) value;
        } else {
            return NULL_VALUE;
        }
        return name;
    }

    @SuppressWarnings("unchecked")
    private static <U extends Comparable<? super U>> U convertEnumerationValue(TableGroupingFieldQuery query, Object value) {
        String name;
        if (value instanceof IEnum) {
            name = ((IEnum<?>) value).name();
        } else if (value instanceof String) {
            name = (String) value;
        } else {
            return (U) NULL_VALUE;
        }
        String dataDictionaryValue = query.getDataDictionaryValue(name);
        if (dataDictionaryValue == null) {
            return (U) name;
        }
        if (query.isNumericDataDictionary()) {
            return (U) Long.valueOf(dataDictionaryValue);
        }
        return (U) dataDictionaryValue;
    }
}
