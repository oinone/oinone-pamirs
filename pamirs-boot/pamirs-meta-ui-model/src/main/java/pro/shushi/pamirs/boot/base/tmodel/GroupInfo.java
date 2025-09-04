package pro.shushi.pamirs.boot.base.tmodel;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.D;
import pro.shushi.pamirs.meta.base.TransientModel;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Gesi at 15:46 on 2025/9/1
 */
@Model(displayName = "分组信息")
@Model.model(GroupInfo.MODEL_MODEL)
public class GroupInfo<T extends D> extends TransientModel {

    public static final String MODEL_MODEL = "base.GroupInfo";

    @Field(displayName = "字段名")
    private String field;

    @Field(displayName = "当前分组下数据总数")
    private Long dataCount;

    @Field(displayName = "当前分组数据统计值", summary = "转换成Json字符串")
    private String dataStatisticStr;

    @Field(displayName = "当前分组值", summary = "转换成字符串")
    private String valueStr;

    @Field(displayName = "当前分组数据", summary = "转换成Json字符串")
    private String dataListStr;

    @Field(displayName = "下一级分组信息")
    private List<GroupInfo<T>> groups;

    /**
     * 当前分组数据统计值
     */
    private Map<String, Object> dataStatistic;

    /**
     * 当前分组的值
     */
    private Object value;

    /**
     * 当前分组的数据
     */
    private List<T> dataList;

    /**
     * 当前分组数据
     */
    private T groupData;

    /**
     * 当前分组信息所依赖的分组字段信息
     */
    private GroupField groupField;

    /**
     * 分组路径
     */
    private List<GroupPathNode> groupPath;

    public static String stringifyValue(GroupInfo<?> groupInfo, Object value) {
        if (value == null) {
            return null;
        }
        return value.toString();
    }

    public static String stringifyStatisticResult(Grouping<?> group, GroupInfo<?> groupInfo, Map<String, Object> dataStatistic) {
        if (dataStatistic == null) {
            return null;
        }
        return dataStatistic.toString();
    }

    public static class GroupPathNode {

        public GroupField field;

        public Object value;

        public GroupPathNode(GroupField field, Object value) {
            this.field = field;
            this.value = value;
        }

        @Override
        public String toString() {
            if (value == null) {
                return field + " - null";
            }
            return field + " - " + value;
        }

        @Override
        public int hashCode() {
            if (value == null) {
                return field.hashCode();
            }
            return field.hashCode() & value.hashCode();
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

}
