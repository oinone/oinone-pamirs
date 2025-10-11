package pro.shushi.pamirs.boot.base.tmodel;

import pro.shushi.pamirs.boot.base.utils.GroupingUtils;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.base.D;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

import java.util.*;

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

    @Field(displayName = "当前分组值", summary = "前端使用的value值，需要转换后设置为realValue")
    private Object value;

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
    private Object realValue;

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

    public void storeValue(Grouping<T> group) {
        ModelFieldConfig modelFieldConfig = group.getModelFieldConfig(getField());
        if (TtypeEnum.ENUM.value().equals(modelFieldConfig.getTtype()) || TtypeEnum.isNumericType(modelFieldConfig.getTtype()) || TtypeEnum.isDateType(modelFieldConfig.getTtype())) {
            setValue(GroupingUtils.stringifyValue(modelFieldConfig, getRealValue()));
        } else if (TtypeEnum.O2O.value().equals(modelFieldConfig.getTtype()) || TtypeEnum.O2M.value().equals(modelFieldConfig.getTtype()) || TtypeEnum.M2O.value().equals(modelFieldConfig.getTtype()) || TtypeEnum.M2M.value().equals(modelFieldConfig.getTtype())) {
            Object value = getRealValue();
            if (value instanceof Collection) {
                List<Object> values = new ArrayList<>();
                for (Object valueObj : ((Collection<?>) value)) {
                    values.add(replaceObjectValue(valueObj));
                }
                value = values;
            } else {
                value = replaceObjectValue(value);
            }
            setValue(value);
        } else {
            setValue(getRealValue());
        }
    }

    private Object replaceObjectValue(Object value) {
        if (value == null) {
            return null;
        }
        Map<String, Object> _dMap = null;
        if (value instanceof Map) {
            _dMap = (Map) value;
        } else if (D.class.isAssignableFrom(value.getClass())) {
            _dMap = ((D) value).get_d();
        }
        if (_dMap != null) {
            Map<String, Object> replaceMap = new HashMap<>(_dMap.size());
            _dMap.forEach((k, v) -> {
                if (v instanceof Long) {
                    replaceMap.put(k, v.toString());
                } else {
                    replaceMap.put(k, v);
                }
            });
            return replaceMap;
        } else {
            return value;
        }
    }

}
