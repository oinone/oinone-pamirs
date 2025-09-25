package pro.shushi.pamirs.boot.base.tmodel;

import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.util.JsonUtils;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.Objects;

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
    private String valueStr;

    @JSONField(serialize = false)
    private transient Grouping<T> group;

    @JSONField(serialize = false)
    private transient boolean fromClient = true;

    @JSONField(serialize = false)
    private transient Object value;

    public GroupPathNode() {
        fromClient = true;
    }

    public GroupPathNode(@NotNull Grouping<T> group, @NotNull String field, Object value) {
        setGroup(group);
        setField(field);
        setValue(value);
        fromClient = false;
    }

    public Object getValue() {
        if (fromClient) {
            setValue(GroupInfo.valueFromString(getGroup().getModelFieldConfig(getField()), getValueStr()));
            fromClient = false;
        }
        return value;
    }

    @Override
    public String toString() {
        if (getValue() == null) {
            return getField() + " - null";
        }
        return getField() + " - " + getValue();
    }

    @Override
    public int hashCode() {
        int valueHashCode = getValue() != null ? getValue().hashCode() : 0;
        if (getValue() instanceof Map) {
            valueHashCode = JsonUtils.toJSONString(getValue()).hashCode();
        }
        return Objects.hash(getField().hashCode(), valueHashCode);
    }

    @Override
    public boolean equals(Object obj) {
        GroupPathNode<?> other;
        if (obj instanceof GroupPathNode) {
            other = ((GroupPathNode<?>) obj);
        } else {
            return false;
        }
        if (!StringUtils.equals(other.field, field)) {
            return false;
        }
        if (getValue() instanceof Map || other.getValue() instanceof Map) {
            if (getValue() instanceof Map && other.getValue() instanceof Map) {
                return Objects.equals(JsonUtils.toJSONString(getValue()), JsonUtils.toJSONString(other.getValue()));
            } else if (getValue() instanceof Map) {
                return Objects.equals(JsonUtils.toJSONString(getValue()), other.getValue());
            } else {
                return Objects.equals(getValue(), JsonUtils.toJSONString(other.getValue()));
            }
        }
        return Objects.equals(getValue(), other.getValue());
    }

}
