package pro.shushi.pamirs.boot.base.tmodel;

import pro.shushi.pamirs.framework.orm.json.PamirsDataUtils;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.base.TransientModel;

import java.util.List;
import java.util.Map;

/**
 * @author Gesi at 15:46 on 2025/9/1
 */
@Model(displayName = "分组信息")
@Model.model(GroupInfo.MODEL_MODEL)
public class GroupInfo<T> extends TransientModel {

    public static final String MODEL_MODEL = "base.GroupInfo";

    @Field(displayName = "是否为数据节点")
    private Boolean isLeaf;

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
    private GroupPath<T> groupPath;

    public static String stringifyValue(ModelFieldConfig fieldConfig, Object value) {
        if (value == null) {
            return null;
        }
        return value.toString();
    }

    public static Object valueFromString(ModelFieldConfig fieldConfig, Object valueStr) {
        if (valueStr == null) {
            return null;
        }
        return valueStr;
    }

    public static String stringifyStatisticResult(Grouping<?> group, GroupInfo<?> groupInfo, Map<String, Object> dataStatistic) {
        if (dataStatistic == null) {
            return null;
        }
        return dataStatistic.toString();
    }

    public static String stringifyDataList(Grouping<?> group, GroupInfo<?> groupInfo, List<?> dataList) {
        if (dataList == null) {
            return null;
        }
        return PamirsDataUtils.toJSONString(group.getModel(), dataList);
    }

}
