package pro.shushi.pamirs.meta.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ValueFilter;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class JsonUtils {

    public static Object parseObject(String text) {
        return JSON.parseObject(text);
    }

    public static <T> T parseObject(String text, TypeReference<T> typeReference) {
        return JSON.parseObject(text, typeReference, new Feature[0]);
    }

    public static <T> T parseObject(String text, Class<T> clazz) {
        return JSON.parseObject(text, clazz, new Feature[0]);
    }

    public static <T> T parseObject(String text, Type type) {
        return JSON.parseObject(text, type, new Feature[0]);
    }

    public static <T> T parseMap2Object(Map<String, Object> map, Class<T> clazz) {
        return parseObject(toJSONString(map), clazz);
    }

    public static <T> T parseMap2Object(Map<String, Object> map, TypeReference<T> reference) {
        return parseObject(toJSONString(map), reference);
    }

    public static Map<String, Object> parseObject2Map(Object object) {
        return parseMap(toJSONString(object));
    }

    public static List<Object> parseObjectList(String json) {
        return JSONObject.parseArray(json);
    }

    public static <T> List<T> parseObjectList(String json, Class<T> clazz) {
        return JSONObject.parseArray(json, clazz);
    }

    public static Map<String, Object> parseMap(String json) {
        return JSON.parseObject(json, new TypeReference<Map<String, Object>>() {
        }.getType());
    }

    public static List<Map<String, Object>> parseMapList(String json) {
        return JSON.parseObject(json, new TypeReference<List<Map<String, Object>>>() {
        }.getType());
    }

    public static List<Map<String, Object>> parseObjectList2MapList(Object object) {
        return parseMapList(toJSONString(object));
    }

    public static <T> List<T> parseMapList2ObjectList(List<Map<String, Object>> mapList, Class<T> clazz) {
        return parseObjectList(toJSONString(mapList), clazz);
    }

    public static String toJSONString(Object object) {
        return toJSONString(object, SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteDateUseDateFormat);
    }

    public static String toJSONString(Object object, boolean prettyFormat) {
        if (prettyFormat) {
            return toJSONString(object, SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteDateUseDateFormat, SerializerFeature.PrettyFormat);
        }
        return toJSONString(object);
    }

    public static String toJSONString(Object object, SerializerFeature... features) {
        return JSON.toJSONString(object, (ValueFilter) (o, s, value) -> {
            if (null != value && value instanceof BigDecimal) {
                if (new BigDecimal(((BigDecimal) value).intValue()).compareTo((BigDecimal) value) == 0) {
                    return ((BigDecimal) value).setScale(2);
                }
            }
            return value;
        }, features);
    }
}
