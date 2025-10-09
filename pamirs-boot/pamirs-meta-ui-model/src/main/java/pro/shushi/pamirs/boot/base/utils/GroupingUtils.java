package pro.shushi.pamirs.boot.base.utils;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.ParameterizedTypeImpl;
import pro.shushi.pamirs.boot.base.enmu.GroupingExpEnumerate;
import pro.shushi.pamirs.framework.orm.json.PamirsDataUtils;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.util.FieldUtils;
import pro.shushi.pamirs.meta.util.JsonUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 分组工具类
 *
 * @author Gesi at 11:40 on 2025/10/9
 */
public class GroupingUtils {

    public static boolean isRelationGroupField(ModelFieldConfig modelFieldConfig) {
        String ttype = modelFieldConfig.getTtype();
        return !TtypeEnum.isBasicType(ttype) && !TtypeEnum.MONEY.value().equals(ttype) && !TtypeEnum.ENUM.value().equals(ttype) && !Boolean.TRUE.equals(modelFieldConfig.getStore());
    }

    /**
     * 序列化分组值放到valueStr里返回
     */
    public static String stringifyValue(ModelFieldConfig fieldConfig, Object value) {
        if (value == null) {
            return null;
        }

        String model = fieldConfig.getModel();
        if (TtypeEnum.ENUM.value().equals(fieldConfig.getTtype())) {
            Object modelObject = PamirsDataUtils.jsonObjectToModelObject(model, new JSONObject());
            FieldUtils.setFieldValue(modelObject, fieldConfig.getField(), value);
            Map<String, Object> jsonObject = PamirsDataUtils.modelObjectToJsonObject(model, modelObject);
            Object enumValue = jsonObject.get(fieldConfig.getField());
            if (Boolean.TRUE.equals(fieldConfig.getMulti())) {
                return JsonUtils.toJSONString(enumValue);
            } else {
                return enumValue.toString();
            }
        }
        String jsonValue = JsonUtils.toJSONString(value);
        if (TtypeEnum.isDateType(fieldConfig.getTtype())) {
            if (jsonValue.startsWith("\"")) {
                jsonValue = jsonValue.substring(1, jsonValue.length() - 1);
            }
            if (jsonValue.endsWith("\"")) {
                jsonValue = jsonValue.substring(0, jsonValue.length() - 1);
            }
        }
        return jsonValue;
    }

    /**
     * 从valueStr里反序列化分组值放value
     */
    public static Object valueFromString(ModelFieldConfig fieldConfig, Object value) {
        if (value == null) {
            return null;
        }
        String model = fieldConfig.getModel();

        if (TtypeEnum.ENUM.value().equals(fieldConfig.getTtype())) {
            JSONObject jsonObject = new JSONObject();
            if (Boolean.TRUE.equals(fieldConfig.getMulti())) {
                jsonObject.put(fieldConfig.getField(), JsonUtils.parseObject(JsonUtils.toJSONString(value)));
            } else {
                jsonObject.put(fieldConfig.getField(), value);
            }
            Object modelObject = PamirsDataUtils.jsonObjectToModelObject(model, jsonObject);
            return FieldUtils.getFieldValue(modelObject, fieldConfig.getField());
        }
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

        Object realValue;
        if (generics.isEmpty()) {
            realValue = JsonUtils.parseObject(JsonUtils.toJSONString(value), clazz);
        } else {
            Type type = new ParameterizedTypeImpl(
                    generics.toArray(new Type[0]),
                    null,
                    clazz
            );
            realValue = JsonUtils.parseObject(JsonUtils.toJSONString(value), type);
        }
        return realValue;
    }

}
