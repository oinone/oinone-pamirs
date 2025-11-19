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
        String model = queryWrapper.getModel();
        List<TableGroupingFieldQuery> queryList = new ArrayList<>();
        List<GroupingField> fields = wrapper.getFields();
        if (fields.size() == 1) {
            queryList.add(new TableGroupingFieldQuery(model, fields.get(0), wrapper.getStatisticField()));
        } else {
            TableGroupingFieldQuery parent = null;
            int lastIndex = fields.size() - 1;
            for (int i = 0; i < lastIndex; i++) {
                GroupingField field = fields.get(i);
                parent = new TableGroupingFieldQuery(model, field, parent);
                queryList.add(parent);
            }
            queryList.add(new TableGroupingFieldQuery(model, fields.get(lastIndex), wrapper.getStatisticField(), parent));
        }
        return queryList;
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

    public static GroupingDataWrapper computeIfAbsent(Map<String, GroupingDataWrapper> groupingDataMap, TableGroupingFieldQuery query, Object data, boolean isLeaf) {
        Object value = getGroupValue(query, data);
        GroupingValueSerializeResult serializeResult = serializeGroupValue(query, value);
        return groupingDataMap.computeIfAbsent(getGroupKeyByValue(query, value),
                key -> {
                    GroupingData groupingData = new GroupingData();
                    groupingData.setField(query.getField());
                    groupingData.setValue(serializeResult.value);
                    groupingData.setIsLeaf(isLeaf);
                    if (serializeResult.isJsonValue != null) {
                        groupingData.setIsJsonValue(serializeResult.isJsonValue);
                    }
                    return new GroupingDataWrapper(query, key, groupingData, value);
                });
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
            } else if (query.isRelationManyField()) {
                List<String> keys = new ArrayList<>();
                for (Object item : coll) {
                    keys.add(generatorObjectKey(item, query.getReferenceFields()));
                }
                return String.join(CharacterConstants.SEPARATOR_OCTOTHORPE, keys);
            }
        } else if (query.isRelationOneField()) {
            return generatorObjectKey(value, query.getReferenceFields());
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
                return coll.stream()
                        .map(v -> convertEnumerationValue(query, v))
                        .sorted()
                        .map(String::valueOf)
                        .collect(Collectors.joining(CharacterConstants.SEPARATOR_COMMA));
            } else if (query.isRelationManyField()) {
                return coll.stream()
                        .sorted(Comparator.comparing(v -> convertPkValue(query, v)))
                        .map(v -> generatorObjectKey(v, query.getReferenceFields()))
                        .collect(Collectors.joining(CharacterConstants.SEPARATOR_OCTOTHORPE));
            }
        } else if (query.isRelationOneField()) {
            return generatorObjectKey(value, query.getReferenceFields());
        }
        return String.valueOf(value);
    }

    private static String generatorObjectKey(Object value, List<String> fields) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < fields.size(); i++) {
            String field = fields.get(i);
            if (i != 0) {
                builder.append(CharacterConstants.SEPARATOR_COMMA);
            }
            Object targetValue = FieldUtils.getFieldValue(value, field);
            if (targetValue == null || (targetValue instanceof String && StringUtils.isBlank((String) targetValue))) {
                targetValue = NULL_VALUE;
            }
            builder.append(targetValue);
        }
        return builder.toString();
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
            } else if (query.isRelationManyField()) {
                value = coll.stream()
                        .sorted(Comparator.comparing(v -> convertPkValue(query, v)))
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

    private static GroupingValueSerializeResult serializeGroupValue(TableGroupingFieldQuery query, Object value) {
        if (NullValue.INSTANCE.equals(value)) {
            return new GroupingValueSerializeResult(NullValue.INSTANCE);
        }
        if (query.isMulti()) {
            Collection<?> coll = (Collection<?>) value;
            if (query.isEnumField()) {
                return new GroupingValueSerializeResult(coll.stream()
                        .map(TableGroupingDataHelper::convertEnumerationName)
                        .collect(Collectors.toList()));
            } else if (query.isRelationManyField()) {
                return new GroupingValueSerializeResult(PamirsDataUtils.toJSONString(query.getReferences(), value), true);
            }
        } else if (query.isEnumField()) {
            return new GroupingValueSerializeResult(convertEnumerationName(value));
        } else if (query.isRelationOneField()) {
            return new GroupingValueSerializeResult(PamirsDataUtils.toJSONString(query.getReferences(), value), true);
        }
        return new GroupingValueSerializeResult(value);
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

    @SuppressWarnings("unchecked")
    private static <U extends Comparable<? super U>> U convertPkValue(TableGroupingFieldQuery query, Object v) {
        List<String> pks = query.getPks();
        List<String> pkValues = new ArrayList<>();
        for (String pk : pks) {
            Object pkValue = FieldUtils.getFieldValue(v, pk);
            if (pkValue == null) {
                pkValue = NULL_VALUE;
            }
            pkValues.add(String.valueOf(pkValue));
        }
        return (U) pkValues.stream().sorted().collect(Collectors.joining(CharacterConstants.SEPARATOR_OCTOTHORPE));
    }

    private static class GroupingValueSerializeResult {

        private final Object value;

        private final Boolean isJsonValue;

        private GroupingValueSerializeResult(Object value) {
            this(value, null);
        }

        private GroupingValueSerializeResult(Object value, Boolean isJsonValue) {
            this.value = value;
            this.isJsonValue = isJsonValue;
        }
    }
}
