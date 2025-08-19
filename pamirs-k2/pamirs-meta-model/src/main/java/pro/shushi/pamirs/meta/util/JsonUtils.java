package pro.shushi.pamirs.meta.util;

import com.alibaba.fastjson.JSONValidator;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import pro.shushi.pamirs.meta.api.core.orm.serialize.KernelJsonUtils;
import pro.shushi.pamirs.meta.api.core.orm.serialize.config.PamirsParserConfig;
import pro.shushi.pamirs.meta.api.core.orm.serialize.config.PamirsSerializerConfig;
import pro.shushi.pamirs.meta.api.core.orm.serialize.filter.BigDecimalSerializeFilter;
import pro.shushi.pamirs.meta.api.core.orm.serialize.type.EnumUsingValueDeserializer;
import pro.shushi.pamirs.meta.api.core.orm.serialize.type.EnumUsingValueSerializer;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static pro.shushi.pamirs.meta.common.util.TypeReferences.TR_MAP_SO;

public class JsonUtils {

    private static final ParserConfig parserConfig = new PamirsParserConfig(
            EnumUsingValueDeserializer.instance
    );

    private static final SerializeConfig serializeConfig = new PamirsSerializerConfig(
            EnumUsingValueSerializer.instance
    );

    private static final SerializeFilter[] defaultFilters = new SerializeFilter[]{
            new BigDecimalSerializeFilter()
    };

    public static Object parseObject(String text) {
        return KernelJsonUtils.parseObject(text, parserConfig);
    }

    public static <T> T parseObject(String text, TypeReference<T> typeReference) {
        return KernelJsonUtils.parseObject(text, typeReference.getType(), parserConfig);
    }

    public static <T> T parseObject(String text, Class<T> clazz) {
        return KernelJsonUtils.parseObject(text, clazz, parserConfig);
    }

    public static <T> T parseObject(String text, Type type) {
        return KernelJsonUtils.parseObject(text, type, parserConfig);
    }

    public static <T> T parseObject(String text, Type type, Feature... features) {
        return KernelJsonUtils.parseObject(text, type, parserConfig, features);
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
        return KernelJsonUtils.parseObjectList(json, parserConfig);
    }

    public static <T> List<T> parseObjectList(String json, Class<T> clazz) {
        return KernelJsonUtils.parseObjectList(json, clazz, parserConfig);
    }

    public static Map<String, Object> parseMap(String json) {
        return parseObject(json, TR_MAP_SO.getType());
    }

    public static List<Map<String, Object>> parseMapList(String json) {
        return parseObject(json, new TypeReference<List<Map<String, Object>>>() {
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

    public static String toJSONString(Object object, int defaultFeatures, SerializerFeature... features) {
        return KernelJsonUtils.toJSONString(object, serializeConfig, defaultFilters, null, defaultFeatures, features);
    }

    public static String toJSONString(Object object, SerializeFilter[] filters, int defaultFeatures, SerializerFeature... features) {
        return KernelJsonUtils.toJSONString(object, serializeConfig, defaultFilters, filters, defaultFeatures, features);
    }

    public static String toJSONString(Object object, SerializerFeature... features) {
        return KernelJsonUtils.toJSONString(object, serializeConfig, defaultFilters, features);
    }

    public static String toJSONString(Object object, SerializeFilter[] filters, SerializerFeature... features) {
        return KernelJsonUtils.toJSONString(object, serializeConfig, defaultFilters, filters, features);
    }

    public static boolean isJSONString(String json) {
        return validateJSONType(json) != null;
    }

    public static boolean isJSONObject(String json) {
        return JSONValidator.Type.Object.equals(validateJSONType(json));
    }

    public static boolean isJSONArray(String json) {
        return JSONValidator.Type.Array.equals(validateJSONType(json));
    }

    public static JSONValidator.Type validateJSONType(String json) {
        try (JSONValidator validator = JSONValidator.from(json)) {
            if (validator.validate()) {
                return validator.getType();
            }
        } catch (IOException ignored) {
        }
        return null;
    }
}

