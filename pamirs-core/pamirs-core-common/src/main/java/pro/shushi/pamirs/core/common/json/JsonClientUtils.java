package pro.shushi.pamirs.core.common.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONLexer;
import com.alibaba.fastjson.parser.JSONToken;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ValueFilter;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.orm.convert.ClientDataConverter;
import pro.shushi.pamirs.meta.api.core.orm.convert.DataConverter;
import pro.shushi.pamirs.meta.api.core.orm.systems.ModelModelApi;
import pro.shushi.pamirs.meta.api.core.orm.systems.directive.AbstractModelDirectiveApi;
import pro.shushi.pamirs.meta.api.core.orm.template.context.ModelComputeContext;
import pro.shushi.pamirs.meta.api.dto.entity.DataMap;
import pro.shushi.pamirs.meta.common.enmu.IEnum;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.math.BigDecimal.ROUND_HALF_UP;

/**
 * 前端交互JSON序列化工具类
 *
 * @author Adamancy Zhang at 19:41 on 2021-08-30
 */
public class JsonClientUtils {

    private JsonClientUtils() {
        //reject create object
    }

    /**
     * 传给前端前的JSON序列化
     *
     * @param data 任意对象
     * @return JSON字符串
     */
    public static String clientToJSONString(Object data) {
        return Optional.ofNullable(data)
                .map(v -> {
                    ModelModelApi modelApi = Models.api();
                    String model = modelApi.getModel(v);
                    if (StringUtils.isBlank(model)) {
                        model = modelApi.getDataModel(v);
                    }
                    if (StringUtils.isNotBlank(model)) {
                        v = ClientDataConverter.get().out(model, v);
                    }
                    return v;
                })
                .map(JsonClientUtils::toJSONString)
                .orElse(null);
    }

    /**
     * 入库前的前端JSON序列化
     *
     * @param data 工作流节点上下文
     * @return JSON字符串
     */
    public static String toJSONString(Object data) {
        return JSON.toJSONString(data, CommonSerializerConfig.INSTANCE, new SerializeFilter[]{(ValueFilter) (object, name, value) -> {
            if (AbstractModelDirectiveApi.META_BIT.equals(name)) {
                return null;
            }
            if (value instanceof BigDecimal) {
                if (new BigDecimal(((BigDecimal) value).intValue()).compareTo((BigDecimal) value) == 0) {
                    return ((BigDecimal) value).setScale(6, ROUND_HALF_UP);
                }
            }
            if (value instanceof IEnum) {
                return ((IEnum<?>) value).name();
            }
            return value;
        }}, SerializerFeature.DisableCircularReferenceDetect);
    }

    /**
     * 出库后的JSON反序列化，生成后端可操作模型对象
     *
     * @param text  JSON字符串
     * @param model 模型编码
     * @return {@link Map}对象
     */
    public static <T> T clientParseObject(String text, String model) {
        ModelModelApi modelApi = Models.api();
        DataMap dataMap = parseObject(text, DataMap.class);
        modelApi.setModel(dataMap, model);
        modelApi.setDataModel(model, dataMap);
        return ClientDataConverter.get().in(new ModelComputeContext(), model, dataMap);
    }

    /**
     * 出库后的JSON反序列化，生成后端可操作Map对象
     *
     * @param text  JSON字符串
     * @param model 模型编码
     * @return {@link Map}对象
     */
    public static Map<String, Object> clientParseMap(String text, String model) {
        ModelModelApi modelApi = Models.api();
        DataMap dataMap = parseObject(text, DataMap.class);
        modelApi.setModel(dataMap, model);
        modelApi.setDataModel(model, dataMap);
        return Models.orm().mapping(model, BeanDefinitionUtils.getBean(DataConverter.class).out(model, dataMap));
    }

    public static List<Map<String, Object>> clientParseListMap(String text, String model) {
        ModelModelApi modelApi = Models.api();
        List<DataMap> dataMaps = parseListObject(text, DataMap.class);
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (DataMap dataMap : dataMaps) {
            modelApi.setModel(dataMap, model);
            modelApi.setDataModel(model, dataMap);
            resultList.add(Models.orm().mapping(model, BeanDefinitionUtils.getBean(DataConverter.class).out(model, dataMap)));
        }
        return resultList;
    }

    /**
     * 当使用{@link JsonClientUtils#toJSONString(Object)}进行序列化时，需使用此方法进行反序列化
     *
     * @param text  JSON字符串
     * @param clazz 序列化类
     * @param <T>   任意类型
     * @return 序列化类型对象
     */
    public static <T> T parseObject(String text, Class<T> clazz) {
        return JSON.parseObject(text, clazz, CommonParserConfig.INSTANCE);
    }

    public static <T> List<T> parseListObject(String text, Class<T> clazz) {
        return parseArray(text, clazz);
    }

    /**
     * 当使用{@link JsonClientUtils#toJSONString(Object)}进行序列化时，需使用此方法进行反序列化
     *
     * @param text JSON字符串
     * @param type 序列化类型
     * @param <T>  任意类型
     * @return 序列化类型对象
     */
    public static <T> T parseObject(String text, Type type) {
        return JSON.parseObject(text, type, CommonParserConfig.INSTANCE);
    }

    private static <T> List<T> parseArray(String text, Class<T> clazz) {
        if (text == null) {
            return null;
        }
        List<T> list;
        DefaultJSONParser parser = new DefaultJSONParser(text, CommonParserConfig.INSTANCE);
        JSONLexer lexer = parser.lexer;
        int token = lexer.token();
        if (token == JSONToken.NULL) {
            lexer.nextToken();
            list = null;
        } else if (token == JSONToken.EOF && lexer.isBlankInput()) {
            list = null;
        } else {
            list = new ArrayList<T>();
            parser.parseArray(clazz, list);

            parser.handleResovleTask(list);
        }
        parser.close();
        return list;
    }
}
