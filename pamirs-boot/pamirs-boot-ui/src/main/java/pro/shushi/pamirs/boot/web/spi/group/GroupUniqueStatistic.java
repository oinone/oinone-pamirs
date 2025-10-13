package pro.shushi.pamirs.boot.web.spi.group;

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
                Object relationData = data;
                if (data == null) {
                    relationDataList.add(null);
                    continue;
                }
                List<String> referenceFields = modelFieldConfig.getReferenceFields();
                if (TtypeEnum.O2O.value().equals(modelFieldConfig.getTtype()) || TtypeEnum.M2O.value().equals(modelFieldConfig.getTtype())) {
                    List<Object> referenceFieldValues = new ArrayList<>(referenceFields.size());
                    for (String referenceField : referenceFields) {
                        Object fieldValue = FieldUtils.getFieldValue(data, referenceField);
                        if (fieldValue instanceof Number) {
                            if (fieldValue instanceof BigDecimal) {
                                fieldValue = ((BigDecimal) fieldValue).stripTrailingZeros().toPlainString();
                            } else {
                                fieldValue = fieldValue.toString();
                            }
                        }
                        referenceFieldValues.add(fieldValue);
                    }
                    if (referenceFieldValues.stream().noneMatch(Objects::nonNull)) {
                        relationDataList.add(null);
                        continue;
                    }
                    relationData = referenceFieldValues;
                } else if (TtypeEnum.O2M.value().equals(modelFieldConfig.getTtype()) || TtypeEnum.M2M.value().equals(modelFieldConfig.getTtype())) {
                    List<Object> relationList = new ArrayList<>((Collection) data);
                    List<Object> relationFieldList = new ArrayList<>(relationList.size());
                    for (Object o : relationList) {
                        if (o == null) {
                            relationFieldList.add(null);
                        } else {
                            List<Object> referenceFieldValues = new ArrayList<>(referenceFields.size());
                            for (String referenceField : referenceFields) {
                                Object fieldValue = FieldUtils.getFieldValue(o, referenceField);
                                if (fieldValue instanceof Number) {
                                    if (fieldValue instanceof BigDecimal) {
                                        fieldValue = ((BigDecimal) fieldValue).stripTrailingZeros().toPlainString();
                                    } else {
                                        fieldValue = fieldValue.toString();
                                    }
                                }
                                referenceFieldValues.add(fieldValue);
                            }
                            relationFieldList.add(referenceFieldValues);
                        }
                    }
                    relationData = relationFieldList;
                }

                if (Boolean.TRUE.equals(modelFieldConfig.getMulti())) {
                    if (relationData instanceof Collection) {
                        if (((Collection<?>) relationData).isEmpty()) {
                            return null;
                        }
                        relationData = new HashSet<>((Collection<?>) relationData);
                    }
                }
                relationDataList.add(relationData);
            }
            return unique(relationDataList);
        }
        return unique(dataList);
    }

}
