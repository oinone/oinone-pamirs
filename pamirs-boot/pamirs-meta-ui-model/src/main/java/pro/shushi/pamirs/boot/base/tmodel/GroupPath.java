package pro.shushi.pamirs.boot.base.tmodel;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Gesi at 15:31 on 2025/9/8
 */
@Model(displayName = "分组路径")
@Model.model(GroupPath.MODEL_MODEL)
public class GroupPath<T> extends TransientModel {

    public static final String MODEL_MODEL = "base.GroupPath";

    @Field
    private List<GroupPathNode<T>> nodeList;

    public GroupPath() {
        setNodeList(new ArrayList<>());
    }

    public GroupPath(GroupPath<T> groupPath) {
        setNodeList(new ArrayList<>(groupPath.getNodeList()));
    }

    public GroupPath(List<GroupPathNode<T>> nodeList) {
        setNodeList(new ArrayList<>(nodeList));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupPath<?> groupPath = (GroupPath<?>) o;
        return Objects.equals(getNodeList(), groupPath.getNodeList());
    }

    @Override
    public int hashCode() {
        return getNodeList().hashCode();
    }

    public void addNode(GroupPathNode<T> node) {
        getNodeList().add(node);
    }

    public int size() {
        return getNodeList().size();
    }
}
