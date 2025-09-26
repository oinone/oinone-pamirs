package pro.shushi.pamirs.boot.base.tmodel;

import com.alibaba.fastjson.util.ParameterizedTypeImpl;
import pro.shushi.pamirs.boot.base.enmu.GroupingExpEnumerate;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.util.JsonUtils;

import java.lang.reflect.Type;
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

    /**
     * 序列化分组值放到valueStr里返回
     */
    public static String stringifyValue(ModelFieldConfig fieldConfig, Object value) {
        if (value == null) {
            return null;
        }
        return JsonUtils.toJSONString(value);
    }

    /**
     * 从valueStr里反序列化分组值放value
     */
    public static Object valueFromString(ModelFieldConfig fieldConfig, String valueStr) {
        if (valueStr == null) {
            return null;
        }
        String model = fieldConfig.getModel();
        Class<?> clazz;
        List<Type> generics = new ArrayList<>();
        try {
            clazz = Class.forName(fieldConfig.getLtype());
        } catch (ClassNotFoundException e) {
            throw PamirsException.construct(GroupingExpEnumerate.SYSTEM_ERROR, e).appendMsg(model + "." + fieldConfig.getField() + "的类型" + fieldConfig.getLtype() + "找不到").errThrow();
        }

        if (fieldConfig.getLtypeT() != null) {
            try {
                generics.add(Class.forName(fieldConfig.getLtypeT()));
            } catch (ClassNotFoundException e) {
                throw PamirsException.construct(GroupingExpEnumerate.SYSTEM_ERROR, e).appendMsg(model + "." + fieldConfig.getField() + "的类型泛型" + fieldConfig.getLtypeT() + "找不到").errThrow();
            }
        }

        Object value;
        if (generics.isEmpty()) {
            value = JsonUtils.parseObject(valueStr, clazz);
        } else {
            Type type = new ParameterizedTypeImpl(
                    generics.toArray(new Type[0]),
                    null,
                    List.class
            );
            value = JsonUtils.parseObject(valueStr, type);
        }
        return value;
    }

}
