package pro.shushi.pamirs.boot.base.tmodel;

import com.alibaba.fastjson.annotation.JSONField;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.boot.base.utils.GroupingUtils;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.util.FieldUtils;
import pro.shushi.pamirs.meta.util.JsonUtils;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Gesi at 14:31 on 2025/9/8
 */
@Model(displayName = "分组路径节点")
@Model.model(GroupPathNode.MODEL_MODEL)
public class GroupPathNode<T> extends TransientModel {

    public static final String MODEL_MODEL = "base.GroupPathNode";

    @Field
    private String field;

    @Field
    private Object value;

    @JSONField(serialize = false)
    private transient Grouping<T> group;

    @JSONField(serialize = false)
    private transient boolean fromClient = true;

    @JSONField(serialize = false)
    private transient Object realValue;

    @JSONField(serialize = false)
    private transient Object equalsValue;

    public GroupPathNode() {
        fromClient = true;
    }

    public GroupPathNode(@NotNull Grouping<T> group, @NotNull String field, Object realValue) {
        setGroup(group);
        setField(field);
        setRealValue(realValue);
        fromClient = false;
    }

    public Object getRealValue() {
        if (fromClient) {
            ModelFieldConfig modelFieldConfig = getGroup().getModelFieldConfig(getField());
            if (TtypeEnum.ENUM.value().equals(modelFieldConfig.getTtype()) || TtypeEnum.isNumericType(modelFieldConfig.getTtype()) || TtypeEnum.isDateType(modelFieldConfig.getTtype())) {
                setRealValue(GroupingUtils.valueFromString(modelFieldConfig, getValue()));
            } else {
                setRealValue(getValue());
            }
            fromClient = false;
        }
        return realValue;
    }

    @Override
    public String toString() {
        if (getRealValue() == null) {
            return getField() + " - null";
        }
        return getField() + " - " + getRealValue();
    }

    @Override
    public int hashCode() {
        ModelFieldConfig modelFieldConfig = getGroup().getModelFieldConfig(getField());
        Object value = handleMapOrRelationEqualsValue(modelFieldConfig, getRealValue());
        return Objects.hash(getField().hashCode(), value != null ? value.hashCode() : 0);
    }

    @Override
    public boolean equals(Object obj) {
        GroupPathNode<?> other;
        if (obj instanceof GroupPathNode) {
            other = ((GroupPathNode<?>) obj);
        } else {
            return false;
        }
        if (!StringUtils.equals(other.getField(), getField())) {
            return false;
        }

        ModelFieldConfig modelFieldConfig = getGroup().getModelFieldConfig(getField());
        Object thisValue = getRealValue();
        Object otherValue = other.getRealValue();

        if (getEqualsValue() == null) {
            thisValue = handleMapOrRelationEqualsValue(modelFieldConfig, thisValue);
            setEqualsValue(thisValue);
        } else {
            thisValue = getEqualsValue();
        }
        if (other.getEqualsValue() == null) {
            otherValue = handleMapOrRelationEqualsValue(modelFieldConfig, otherValue);
            other.setEqualsValue(otherValue);
        } else {
            otherValue = other.getEqualsValue();
        }
        return Objects.equals(thisValue, otherValue);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Object handleMapOrRelationEqualsValue(ModelFieldConfig modelFieldConfig, Object realValue) {
        if ("".equals(realValue)) {
            realValue = null;
        }
        if ((TtypeEnum.OBJ.value().equals(modelFieldConfig.getTtype()) || TtypeEnum.MAP.value().equals(modelFieldConfig.getTtype())) && realValue instanceof Map && ((Map) realValue).isEmpty()) {
            realValue = null;
        }
        if (realValue == null || realValue instanceof String) {
            return realValue;
        }
        if (
                !Boolean.TRUE.equals(modelFieldConfig.getMulti()) &&
                        !TtypeEnum.MAP.value().equals(modelFieldConfig.getTtype()) &&
                        !TtypeEnum.O2O.value().equals(modelFieldConfig.getTtype()) &&
                        !TtypeEnum.O2M.value().equals(modelFieldConfig.getTtype()) &&
                        !TtypeEnum.M2O.value().equals(modelFieldConfig.getTtype()) &&
                        !TtypeEnum.M2M.value().equals(modelFieldConfig.getTtype()) &&
                        !TtypeEnum.OBJ.value().equals(modelFieldConfig.getTtype())
        ) {
            if (realValue instanceof Number) {
                if (realValue instanceof BigDecimal) {
                    return ((BigDecimal) realValue).stripTrailingZeros().toPlainString();
                }
                return realValue.toString();
            }
            return realValue;
        }
        if (GroupingUtils.isMemoryGroupField(modelFieldConfig)) {
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
                        Object fieldValue = FieldUtils.getFieldValue(realValue, field);
                        if (fieldValue instanceof Number) {
                            if (fieldValue instanceof BigDecimal) {
                                fieldValue = ((BigDecimal) fieldValue).stripTrailingZeros().toPlainString();
                            } else {
                                fieldValue = fieldValue.toString();
                            }
                            fieldValues.add(fieldValue);
                        }
                    }
                    relationFieldValues.add(fieldValues);
                }
                if (relationFieldValues.stream().noneMatch(Objects::nonNull)) {
                    return null;
                }
                realValue = relationFieldValues;
            } else if (TtypeEnum.O2M.value().equals(modelFieldConfig.getTtype()) || TtypeEnum.M2M.value().equals(modelFieldConfig.getTtype())) {
                if (TtypeEnum.O2M.value().equals(modelFieldConfig.getTtype())) {
                    relationFields = modelFieldConfig.getRelationFields().stream().map(Lists::newArrayList).collect(Collectors.toList());
                } else {
                    relationFields = new ArrayList<>(modelFieldConfig.getRelationFields().size());
                    for (int i = 0; i < modelFieldConfig.getRelationFields().size(); i++) {
                        relationFields.add(Lists.newArrayList(modelFieldConfig.getRelationFields().get(i), modelFieldConfig.getReferenceFields().get(i)));
                    }
                }
                List<Object> relationList = new ArrayList<>((Collection) realValue);
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
                                    fieldValues.add(fieldValue);
                                }
                            }
                            referenceFieldValues.add(fieldValues);
                        }
                        relationFieldList.add(referenceFieldValues);
                    }
                }
                realValue = relationFieldList;
            }
        }

        if (Boolean.TRUE.equals(modelFieldConfig.getMulti())) {
            if (realValue instanceof Collection) {
                if (((Collection<?>) realValue).isEmpty()) {
                    return null;
                }
                realValue = new HashSet<>((Collection<?>) realValue);
            }
            return realValue;
        }

        return JsonUtils.toJSONString(realValue);
    }

}
