package pro.shushi.pamirs.boot.web.spi.group;

import com.google.common.collect.Lists;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.tmodel.GroupInfo;
import pro.shushi.pamirs.boot.base.tmodel.Grouping;
import pro.shushi.pamirs.boot.base.utils.GroupingUtils;
import pro.shushi.pamirs.boot.web.spi.api.GroupStatisticApi;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.util.FieldUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 唯一值
 *
 * @author Gesi at 9:39 on 2025/9/9
 */
@SPI.Service("UNIQUE")
@Component
@Slf4j
public class GroupUniqueStatistic extends AbstractGroupStatisticApi implements GroupStatisticApi {

    @Override
    public <T> Object statistic(Grouping<T> group, GroupInfo<T> groupInfo, String statisticField, List<?> dataList) {
        ModelFieldConfig modelFieldConfig = group.getModelFieldConfig(statisticField);
        if (GroupingUtils.isMemoryGroupField(modelFieldConfig)) {
            List<Object> relationDataList = new ArrayList<>(dataList.size());
            for (Object data : dataList) {
                if (data == null) {
                    relationDataList.add(null);
                    continue;
                }
                List<List<String>> relationFields;
                if (TtypeEnum.O2O.value().equals(modelFieldConfig.getTtype()) || TtypeEnum.M2O.value().equals(modelFieldConfig.getTtype())) {
                    if (TtypeEnum.O2O.value().equals(modelFieldConfig.getTtype())) {
                        relationFields = modelFieldConfig.getReferenceFields().stream().map(Lists::newArrayList).collect(Collectors.toList());
                    } else {
                        relationFields = new ArrayList<>(modelFieldConfig.getRelationFields().size());
                        for (int i = 0; i < modelFieldConfig.getRelationFields().size(); i++) {
                            relationFields.add(Lists.newArrayList(modelFieldConfig.getRelationFields().get(i), modelFieldConfig.getReferenceFields().get(i)));
                        }
                    }
                    List<List<Object>> relationFieldValues = new ArrayList<>(relationFields.size());
                    for (List<String> referenceField : relationFields) {
                        List<Object> fieldValues = new ArrayList<>(referenceField.size());
                        for (String field : referenceField) {
                            Object fieldValue = FieldUtils.getFieldValue(data, field);
                            if (fieldValue instanceof Number) {
                                if (fieldValue instanceof BigDecimal) {
                                    fieldValue = ((BigDecimal) fieldValue).stripTrailingZeros().toPlainString();
                                } else {
                                    fieldValue = fieldValue.toString();
                                }
                            }
                            fieldValues.add(fieldValue);
                        }
                        relationFieldValues.add(fieldValues);
                    }
                    if (relationFieldValues.stream().noneMatch(Objects::nonNull)) {
                        relationDataList.add(null);
                        continue;
                    }
                    data = relationFieldValues;
                } else if (TtypeEnum.O2M.value().equals(modelFieldConfig.getTtype()) || TtypeEnum.M2M.value().equals(modelFieldConfig.getTtype())) {
                    if (TtypeEnum.O2M.value().equals(modelFieldConfig.getTtype())) {
                        relationFields = modelFieldConfig.getRelationFields().stream().map(Lists::newArrayList).collect(Collectors.toList());
                    } else {
                        relationFields = new ArrayList<>(modelFieldConfig.getRelationFields().size());
                        for (int i = 0; i < modelFieldConfig.getRelationFields().size(); i++) {
                            relationFields.add(Lists.newArrayList(modelFieldConfig.getRelationFields().get(i), modelFieldConfig.getReferenceFields().get(i)));
                        }
                    }
                    List<Object> relationList = new ArrayList<>((Collection) data);
                    List<Object> relationFieldList = new ArrayList<>(relationList.size());
                    for (Object o : relationList) {
                        if (o == null) {
                            relationFieldList.add(null);
                        } else {
                            List<List<Object>> referenceFieldValues = new ArrayList<>(relationFields.size());
                            for (List<String> referenceField : relationFields) {
                                List<Object> fieldValues = new ArrayList<>(referenceField.size());
                                for (String field : referenceField) {
                                    Object fieldValue = FieldUtils.getFieldValue(o, field);
                                    if (fieldValue instanceof Number) {
                                        if (fieldValue instanceof BigDecimal) {
                                            fieldValue = ((BigDecimal) fieldValue).stripTrailingZeros().toPlainString();
                                        } else {
                                            fieldValue = fieldValue.toString();
                                        }
                                    }
                                    fieldValues.add(fieldValue);
                                }
                                referenceFieldValues.add(fieldValues);
                            }
                            relationFieldList.add(referenceFieldValues);
                        }
                    }
                    data = relationFieldList;
                }

                if (Boolean.TRUE.equals(modelFieldConfig.getMulti())) {
                    if (data instanceof Collection) {
                        if (((Collection<?>) data).isEmpty()) {
                            data = null;
                        } else {
                            data = new HashSet<>((Collection<?>) data);
                        }
                    }
                }
                relationDataList.add(data);
            }
            return unique(relationDataList);
        }
        return unique(dataList);
    }

}
