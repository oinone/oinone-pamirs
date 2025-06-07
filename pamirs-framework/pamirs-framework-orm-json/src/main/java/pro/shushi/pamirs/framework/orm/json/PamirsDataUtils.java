package pro.shushi.pamirs.framework.orm.json;

import pro.shushi.pamirs.framework.common.utils.ObjectUtils;
import pro.shushi.pamirs.framework.orm.json.api.DataComputeApi;
import pro.shushi.pamirs.framework.orm.json.container.NamedProcessorHolder;
import pro.shushi.pamirs.framework.orm.json.enmu.DataFeature;
import pro.shushi.pamirs.framework.orm.json.handler.DataBigIntegerHandler;
import pro.shushi.pamirs.framework.orm.json.handler.DataBooleanHandler;
import pro.shushi.pamirs.framework.orm.json.handler.DataDateHandler;
import pro.shushi.pamirs.framework.orm.json.handler.DataEnumHandler;
import pro.shushi.pamirs.framework.orm.named.LnameToNameProcessor;
import pro.shushi.pamirs.framework.orm.named.NameToLnameProcessor;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.orm.systems.orm.OrmProcessorHolder;
import pro.shushi.pamirs.meta.api.core.orm.template.DataComputeTemplate;
import pro.shushi.pamirs.meta.api.core.orm.template.function.ModelAfterComputeApi;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.base.D;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.util.JsonUtils;

import java.io.Serializable;
import java.util.Map;

/**
 * Pamirs模型map与json互转工具类
 * <p>
 * 2021/9/23 10:28 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class PamirsDataUtils {

    public final static int DEFAULT_FEATURE;

    static {
        int features = 0;
        features |= DataFeature.IgnoreMetaBit.getMask();
        DEFAULT_FEATURE = features;
    }

    public static <T> T parseModelObject(String model, String data, DataFeature... features) {
        Object jsonObject = JsonUtils.parseObject(data);
        return jsonObjectToModelObject(model, jsonObject, features);
    }

    public static <T> T parseModelMap(String model, String data, DataFeature... features) {
        Object jsonObject = JsonUtils.parseObject(data);
        return jsonObjectToMapObject(model, jsonObject, features);
    }

    public static <T> String toJSONString(String model, T data, DataFeature... features) {
        T t = modelObjectToJsonObject(model, data, features);
        return JsonUtils.toJSONString(t);
    }

    public static <T> T parseModelObjectHandlerEnum(String model, Object obj, DataFeature... features) {
        int feature = generateFeature(features);
        return jsonObjectHandlerEnum(model, obj, PamirsDataUtils::jsonObjectToMapObject, (oModel, oObj) -> oObj, feature);
    }

    public static <T> T modelObjectToJsonObject(String model, Object obj, DataFeature... features) {
        int feature = generateFeature(features);
        return objectToJsonObject(model, obj, feature);
    }

    public static <T> T jsonObjectToModelObject(String model, Object obj, DataFeature... features) {
        int feature = generateFeature(features);
        return jsonObjectToModelObject(model, obj, feature);
    }

    private static <T> T jsonObjectToMapObject(String model, Object obj, DataFeature... features) {
        int feature = generateFeature(features);
        return jsonObjectToMapObject(model, obj, feature);
    }

    private static <T> T jsonObjectToModelObject(String model, Object obj, int features) {
        return jsonObjectToObject(model, obj, (oModel, oObj, feat) -> jsonObjectToModelObject(oModel, oObj, features),
                (oModel, oObj) -> OrmProcessorHolder.objectingProcessor().after(oModel, oObj), features);
    }

    private static <T> T jsonObjectToMapObject(String model, Object obj, int features) {
        return jsonObjectToObject(model, obj, PamirsDataUtils::jsonObjectToMapObject, (oModel, oObj) -> oObj, features);
    }

    private static <T> T objectToJsonObject(String model, Object obj, int features) {
        obj = ObjectUtils.clone((Serializable) obj);
        NameToLnameProcessor nameToLnameProcessor = NamedProcessorHolder.getNameToLnameProcessor();
        return DataComputeTemplate.getInstance().compute(model, obj,
                (oModel, oObj) -> objectToJsonObject(oModel, oObj, features),
                (oModel, oObj) -> oObj,// 模型化
                (oModel, oObj) -> Map.class.isAssignableFrom(oObj.getClass()) ? oObj : ((D) oObj).get_d(),// map化
                (context, fieldConfig, dMap) -> {
                    // 清除数据指令
                    if (DataFeature.isEnabled(features, DataFeature.IgnoreMetaBit)) {
                        Models.modelDirective().clear(dMap);
                    }
                    // 类型转换
                    String ttype = ttype(fieldConfig);
                    switch (ttype) {
                        case "integer":
                            DataBigIntegerHandler.stringify(fieldConfig, dMap, features);
                            break;
                        case "datetime":
                        case "date":
                        case "time":
                        case "year":
                            DataDateHandler.stringify(fieldConfig, dMap, features);
                            break;
                        case "enum":
                            DataEnumHandler.stringify(fieldConfig, dMap, features);
                            break;
                        case "o2o":
                        case "o2m":
                        case "m2o":
                        case "m2m": {
                            Object value = dMap.get(fieldConfig.getLname());
                            if (null != value && TtypeEnum.isRelationType(ttype)) {
                                dMap.put(fieldConfig.getLname(), objectToJsonObject(fieldConfig.getReferences(), value, features));
                            }
                            break;
                        }
                    }
                },// 字段类型处理
                (context, fieldConfig, dMap) -> nameToLnameProcessor.convert(fieldConfig, dMap)// 字段名称转化
        );
    }

    @SuppressWarnings("rawtypes")
    private static <T> T jsonObjectToObject(String model, Object obj,
                                            DataComputeApi dataComputeApi,
                                            ModelAfterComputeApi modelAfterComputeProcessor,
                                            int features) {
        LnameToNameProcessor lnameToNameProcessor = NamedProcessorHolder.getLnameToNameProcessor();
        return DataComputeTemplate.getInstance().compute(model, obj,
                (oModel, oObj) -> dataComputeApi.run(oModel, oObj, features),
                (oModel, oObj) -> {
                    Models.api().setModel(oObj, oModel);
                    return oObj;
                },// 模型化
                modelAfterComputeProcessor,// 对象化
                (context, fieldConfig, dMap) -> lnameToNameProcessor.convert(fieldConfig, dMap),// 技术名称转化
                (context, fieldConfig, dMap) -> {
                    // 类型转换
                    String ttype = ttype(fieldConfig);
                    switch (ttype) {
                        case "integer":
                            DataBigIntegerHandler.toBigInteger(fieldConfig, dMap, features);
                            break;
                        case "bool":
                            DataBooleanHandler.toBoolean(fieldConfig, dMap, features);
                            break;
                        case "datetime":
                        case "date":
                        case "time":
                        case "year":
                            DataDateHandler.toDate(fieldConfig, dMap, features);
                            break;
                        case "enum":
                            DataEnumHandler.toEnum(fieldConfig, dMap, features);
                            break;
                        case "o2o":
                        case "o2m":
                        case "m2o":
                        case "m2m": {
                            Object value = dMap.get(fieldConfig.getLname());
                            if (null != value && TtypeEnum.isRelationType(ttype)) {
                                dMap.put(fieldConfig.getLname(), dataComputeApi.run(fieldConfig.getReferences(), value, features));
                            }
                            break;
                        }
                    }
                }// 字段类型处理
        );
    }

    @SuppressWarnings("rawtypes")
    private static <T> T jsonObjectHandlerEnum(String model, Object obj,
                                               DataComputeApi dataComputeApi,
                                               ModelAfterComputeApi modelAfterComputeProcessor,
                                               int features) {
        return DataComputeTemplate.getInstance().compute(model, obj,
                (oModel, oObj) -> dataComputeApi.run(oModel, oObj, features),
                (oModel, oObj) -> {
                    Models.api().setModel(oObj, oModel);
                    return oObj;
                },// 模型化
                modelAfterComputeProcessor,// 对象化
                (context, fieldConfig, dMap) -> {
                    // 类型转换
                    String ttype = ttype(fieldConfig);
                    if (TtypeEnum.ENUM.value().equals(ttype)) {
                        DataEnumHandler.stringify(fieldConfig, dMap, features);
                    }
                }
        );
    }

    private static int generateFeature(DataFeature... features) {
        int featureValues = DEFAULT_FEATURE;
        for (DataFeature feature : features) {
            featureValues = DataFeature.config(featureValues, feature, true);
        }
        return featureValues;
    }

    private static String ttype(ModelFieldConfig fieldConfig) {
        // 处理引用字段类型
        String ttype = fieldConfig.getTtype();
        if (TtypeEnum.isRelatedType(fieldConfig.getTtype())) {
            ttype = fieldConfig.getRelatedTtype();
        }
        return ttype;
    }

}
