package pro.shushi.pamirs.boot.base.tmodel;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;

import javax.validation.constraints.NotNull;
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

    public Grouping<T> group;

    public boolean fromClient = true;

    public Object value;

    public GroupPathNode() {
        fromClient = true;
    }

    public GroupPathNode(@NotNull Grouping<T> group, @NotNull String field, Object value) {
        setGroup(group);
        setField(field);
        setValue(value);
        setFromClient(false);
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
        return Objects.hash(getField().hashCode(), getValue() != null ? getValue().hashCode() : 0);
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
        return Objects.equals(getValue(), other.getValue());
    }

}
