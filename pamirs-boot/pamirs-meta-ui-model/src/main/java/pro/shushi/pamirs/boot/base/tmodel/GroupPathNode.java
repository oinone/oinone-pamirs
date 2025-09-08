package pro.shushi.pamirs.boot.base.tmodel;

import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * @author Gesi at 14:31 on 2025/9/8
 */
public class GroupPathNode {

    public GroupField field;

    public Object value;

    public GroupPathNode(@NotNull GroupField field, Object value) {
        this.field = field;
        this.value = value;
    }

    @Override
    public String toString() {
        if (value == null) {
            return field.getField() + " - null";
        }
        return field.getField() + " - " + value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(field.getField().hashCode(), value != null ? value.hashCode() : 0);
    }

    @Override
    public boolean equals(Object obj) {
        GroupPathNode other;
        if (obj instanceof GroupPathNode) {
            other = ((GroupPathNode) obj);
        } else {
            return false;
        }
        return StringUtils.equals(other.field.getField(), field.getField()) && Objects.equals(other.value, value);
    }

}
