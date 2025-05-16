package pro.shushi.pamirs.meta.api.core.orm.serialize;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.*;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.commons.lang3.ArrayUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static pro.shushi.pamirs.meta.common.util.TypeReferences.TR_MAP_SO;

/**
 * 内核JSON工具类
 * <p>
 * 2021/9/27 12:02 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Slf4j
public class KernelJsonUtils {

    public final static String NULL_VALUE_USING_STRING = "nil";

    public static Object parseObject(String text, ParserConfig parserConfig) {
        return JSON.parse(text, parserConfig);
    }

    public static <T> T parseObject(String text, TypeReference<T> typeReference, ParserConfig parserConfig) {
        try {
            return JSON.parseObject(text, typeReference.getType(), parserConfig, new Feature[0]);
        } catch (JSONException e) {
            log.error(MessageFormat.format("JSON:{0},TYPE:{1}", text, typeReference.getType().getTypeName()));
            throw e;
        }
    }

    public static <T> T parseObject(String text, Class<T> clazz, ParserConfig parserConfig) {
        try {
            return JSON.parseObject(text, clazz, parserConfig, new Feature[0]);
        } catch (JSONException e) {
            log.error(MessageFormat.format("JSON:{0},TYPE:{1}", text, clazz.getName()));
            throw e;
        }
    }

    public static <T> T parseObject(String text, Type type, ParserConfig parserConfig) {
        try {
            return JSON.parseObject(text, type, parserConfig, new Feature[0]);
        } catch (JSONException e) {
            log.error(MessageFormat.format("JSON:{0},TYPE:{1}", text, type.getTypeName()));
            throw e;
        }
    }

    public static <T> T parseObject(String text, Type type, ParserConfig parserConfig, Feature... features) {
        try {
            return JSON.parseObject(text, type, parserConfig, features);
        } catch (JSONException e) {
            log.error(MessageFormat.format("JSON:{0},TYPE:{1}", text, type.getTypeName()));
            throw e;
        }
    }

    public static <T> T parseMap2Object(Map<String, Object> map, Class<T> clazz, ParserConfig parserConfig, SerializeConfig serializeConfig, SerializeFilter[] defaultFilters) {
        return parseObject(toJSONString(map, serializeConfig, defaultFilters), clazz, parserConfig);
    }

    public static <T> T parseMap2Object(Map<String, Object> map, TypeReference<T> reference, ParserConfig parserConfig, SerializeConfig serializeConfig, SerializeFilter[] defaultFilters) {
        return parseObject(toJSONString(map, serializeConfig, defaultFilters), reference, parserConfig);
    }

    public static Map<String, Object> parseObject2Map(Object object, ParserConfig parserConfig, SerializeConfig serializeConfig, SerializeFilter[] defaultFilters) {
        return parseMap(toJSONString(object, serializeConfig, defaultFilters), parserConfig);
    }

    public static List<Object> parseObjectList(String json, ParserConfig parserConfig) {
        try {
            return parseArray(json, parserConfig);
        } catch (JSONException e) {
            log.error(MessageFormat.format("JSON:{0},TYPE:List", json));
            throw e;
        }
    }

    public static <T> List<T> parseObjectList(String json, Class<T> clazz, ParserConfig parserConfig) {
        try {
            return parseArray(json, clazz, parserConfig);
        } catch (JSONException e) {
            log.error(MessageFormat.format("JSON:{0},TYPE:{1}", json, clazz.getName()));
            throw e;
        }
    }

    public static Map<String, Object> parseMap(String json, ParserConfig parserConfig) {
        try {
            return parseObject(json, TR_MAP_SO.getType(), parserConfig);
        } catch (JSONException e) {
            log.error(MessageFormat.format("JSON:{0},TYPE:Map<String, Object>", json));
            throw e;
        }
    }

    public static List<Map<String, Object>> parseMapList(String json, ParserConfig parserConfig) {
        try {
            return parseObject(json, new TypeReference<List<Map<String, Object>>>() {
            }.getType(), parserConfig);
        } catch (JSONException e) {
            log.error(MessageFormat.format("JSON:{0},TYPE:List<Map<String, Object>>", json));
            throw e;
        }
    }

    public static List<Map<String, Object>> parseObjectList2MapList(Object object,
                                                                    ParserConfig parserConfig,
                                                                    SerializeConfig serializeConfig,
                                                                    SerializeFilter[] defaultFilters) {
        return parseMapList(toJSONString(object, serializeConfig, defaultFilters), parserConfig);
    }

    public static <T> List<T> parseMapList2ObjectList(List<Map<String, Object>> mapList, Class<T> clazz,
                                                      ParserConfig parserConfig,
                                                      SerializeConfig serializeConfig,
                                                      SerializeFilter[] defaultFilters) {
        return parseObjectList(toJSONString(mapList, serializeConfig, defaultFilters), clazz, parserConfig);
    }

    public static String toJSONString(Object object, SerializeConfig serializeConfig, SerializeFilter[] defaultFilters) {
        return toJSONString(object, serializeConfig, defaultFilters, SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteDateUseDateFormat);
    }

    public static String toJSONString(Object object, SerializeConfig serializeConfig, SerializeFilter[] defaultFilters, boolean prettyFormat) {
        if (prettyFormat) {
            return toJSONString(object, serializeConfig, defaultFilters, SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteDateUseDateFormat, SerializerFeature.PrettyFormat);
        }
        return toJSONString(object, serializeConfig, defaultFilters);
    }

    public static String toJSONString(Object object,
                                      SerializeConfig serializeConfig,
                                      SerializeFilter[] defaultFilters,
                                      SerializeFilter[] filters,
                                      SerializerFeature... features) {
        filters = ArrayUtils.addAll(filters, defaultFilters);
        return toJSONString(object, serializeConfig, filters, features);
    }

    public static String toJSONString(Object object,
                                      SerializeConfig serializeConfig,
                                      SerializeFilter[] defaultFilters,
                                      SerializerFeature... features) {
        return JSON.toJSONString(object, serializeConfig, defaultFilters, features);
    }

    public static String toJSONString(Object object,
                                      SerializeConfig serializeConfig,
                                      SerializeFilter[] defaultFilters,
                                      int defaultFeatures,
                                      SerializerFeature... features) {
        return JSON.toJSONString(object, serializeConfig, defaultFilters, null, defaultFeatures, features);
    }

    public static String toJSONString(Object object,
                                      SerializeConfig serializeConfig,
                                      SerializeFilter[] defaultFilters,
                                      SerializeFilter[] filters,
                                      int defaultFeatures,
                                      SerializerFeature... features) {
        filters = ArrayUtils.addAll(filters, defaultFilters);
        return JSON.toJSONString(object, serializeConfig, filters, null, defaultFeatures, features);
    }

    private static JSONArray parseArray(String text, ParserConfig parserConfig) {
        if (text == null) {
            return null;
        }

        DefaultJSONParser parser = new DefaultJSONParser(text, parserConfig);

        JSONArray array;

        JSONLexer lexer = parser.lexer;
        if (lexer.token() == JSONToken.NULL) {
            lexer.nextToken();
            array = null;
        } else if (lexer.token() == JSONToken.EOF) {
            array = null;
        } else {
            array = new JSONArray();
            parser.parseArray(array);

            parser.handleResovleTask(array);
        }

        parser.close();

        return array;
    }

    private static <T> List<T> parseArray(String text, Class<T> clazz, ParserConfig parserConfig) {
        if (text == null) {
            return null;
        }

        List<T> list;

        DefaultJSONParser parser = new DefaultJSONParser(text, parserConfig);
        JSONLexer lexer = parser.lexer;
        int token = lexer.token();
        if (token == JSONToken.NULL) {
            lexer.nextToken();
            list = null;
        } else if (token == JSONToken.EOF && lexer.isBlankInput()) {
            list = null;
        } else {
            list = new ArrayList<>();
            parser.parseArray(clazz, list);

            parser.handleResovleTask(list);
        }

        parser.close();

        return list;
    }

}

