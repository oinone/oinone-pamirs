package pro.shushi.pamirs.boot.base.tmodel;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import pro.shushi.pamirs.framework.orm.json.PamirsDataUtils;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.util.FieldUtils;
import pro.shushi.pamirs.meta.util.JsonUtils;

import java.util.*;
import java.util.stream.Collectors;

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

    public static String stringifyValue(ModelFieldConfig fieldConfig, Object value) {
        if (value == null) {
            return null;
        }
        String model = fieldConfig.getModel();
        Object modelObject = PamirsDataUtils.jsonObjectToModelObject(model, new JSONObject());
        FieldUtils.setFieldValue(modelObject, fieldConfig.getField(), value);
        Map<String, Object> jsonObject = PamirsDataUtils.modelObjectToJsonObject(model, modelObject);
        value = jsonObject.get(fieldConfig.getField());
        if (value == null) {
            return null;
        }
        if (value instanceof Map) {
            return JsonUtils.toJSONString(value);
        } else if (value instanceof List) {
            if (CollectionUtils.isEmpty((List<?>) value)) {
                return JsonUtils.toJSONString(new ArrayList<>());
            } else {
                List<Object> valueList = ((List<?>) value).stream().map(i -> {
                    if (i == null) {
                        return null;
                    } else if (i instanceof Map) {
                        return JsonUtils.toJSONString(i);
                    } else {
                        return i;
                    }
                }).collect(Collectors.toList());
                return JsonUtils.toJSONString(valueList);
            }
        } else if (value instanceof Set) {
            if (CollectionUtils.isEmpty((Set<?>) value)) {
                return JsonUtils.toJSONString(new HashSet<>());
            } else {
                Set<Object> valueList = ((Set<?>) value).stream().map(i -> {
                    if (i == null) {
                        return null;
                    } else if (i instanceof Map) {
                        return JsonUtils.toJSONString(i);
                    } else {
                        return i;
                    }
                }).collect(Collectors.toSet());
                return JsonUtils.toJSONString(valueList);
            }
        }
        return value.toString();
    }

    public static Object valueFromString(ModelFieldConfig fieldConfig, String valueStr) {
        if (valueStr == null) {
            return null;
        }
        String model = fieldConfig.getModel();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(fieldConfig.getField(), valueStr);
        Object modelObject = PamirsDataUtils.parseModelObject(model, JsonUtils.toJSONString(jsonObject));
        Object value = FieldUtils.getFieldValue(modelObject, fieldConfig.getField());
        return value;
    }

}
