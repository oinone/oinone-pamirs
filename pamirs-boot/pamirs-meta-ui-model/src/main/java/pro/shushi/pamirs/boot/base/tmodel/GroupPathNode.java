package pro.shushi.pamirs.boot.base.tmodel;

import com.alibaba.fastjson.annotation.JSONField;
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
import java.util.*;

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

        thisValue = handleMapOrRelationEqualsValue(modelFieldConfig, thisValue);
        otherValue = handleMapOrRelationEqualsValue(modelFieldConfig, otherValue);
        return Objects.equals(thisValue, otherValue);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Object handleMapOrRelationEqualsValue(ModelFieldConfig modelFieldConfig, Object realValue) {
        if (realValue == null && TtypeEnum.isStringType(modelFieldConfig.getTtype())) {
            realValue = "";
        }
        if (realValue == null && TtypeEnum.MAP.value().equals(modelFieldConfig.getTtype())) {
            realValue = JsonUtils.toJSONString(new LinkedHashMap<>());
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
            return realValue;
        }
        if (GroupingUtils.isMemoryGroupField(modelFieldConfig) && !Boolean.TRUE.equals(modelFieldConfig.getStore())) {
            List<String> referenceFields = modelFieldConfig.getReferenceFields();
            if (TtypeEnum.O2O.value().equals(modelFieldConfig.getTtype()) || TtypeEnum.M2O.value().equals(modelFieldConfig.getTtype())) {
                List<Object> referenceFieldValues = new ArrayList<>(referenceFields.size());
                for (String referenceField : referenceFields) {
                    Object fieldValue = FieldUtils.getFieldValue(realValue, referenceField);
                    if (fieldValue instanceof Number) {
                        fieldValue = fieldValue.toString();
                    }
                    referenceFieldValues.add(fieldValue);
                }
                if (referenceFieldValues.stream().noneMatch(Objects::nonNull)) {
                    return null;
                }
                realValue = referenceFieldValues;
            } else if (TtypeEnum.O2M.value().equals(modelFieldConfig.getTtype()) || TtypeEnum.M2M.value().equals(modelFieldConfig.getTtype())) {
                List<Object> relationList = new ArrayList<>((Collection) realValue);
                List<Object> relationFieldList = new ArrayList<>(relationList.size());
                for (Object o : relationList) {
                    if (o == null) {
                        relationFieldList.add(null);
                    } else {
                        List<Object> referenceFieldValues = new ArrayList<>(referenceFields.size());
                        for (String referenceField : referenceFields) {
                            Object fieldValue = FieldUtils.getFieldValue(o, referenceField);
                            if (fieldValue instanceof Number) {
                                fieldValue = fieldValue.toString();
                            }
                            referenceFieldValues.add(fieldValue);
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
