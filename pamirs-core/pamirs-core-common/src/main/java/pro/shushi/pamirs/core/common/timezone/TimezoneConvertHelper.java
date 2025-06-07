package pro.shushi.pamirs.core.common.timezone;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.core.common.ObjectHelper;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.D;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

import java.util.*;

import static pro.shushi.pamirs.core.common.FetchUtil.cast;

/**
 * 时区转换帮助类
 *
 * @author Adamancy Zhang at 12:10 on 2021-09-03
 */
public class TimezoneConvertHelper {

    private TimezoneConvertHelper() {
        //reject create object
    }

    /**
     * 对象时区转换（默认使用系统时区）
     *
     * @param object 任意对象
     * @param to     目标时区
     */
    public static void convert(Object object, TimeZone to) {
        convert(object, TimeZone.getDefault(), to);
    }

    /**
     * 对象时区转换
     *
     * @param object 任意对象
     * @param from   当前时区
     * @param to     目标时区
     */
    public static void convert(Object object, TimeZone from, TimeZone to) {
        TimezoneConverter converter = TimezoneConverter.newInstance(from, to);
        Set<Object> repeatSet = new HashSet<>();
        convert(object, converter, repeatSet);
    }

    /**
     * 对象时区转换
     *
     * @param object    任意对象
     * @param converter 转换器
     */
    public static void convert(Object object, TimezoneConverter converter, Set<Object> repeatSet) {
        if (object == null) {
            return;
        }
        if (object instanceof Collection) {
            convertList(cast(object), converter, repeatSet);
        } else {
            Map<String, Object> map;
            ModelConfig modelConfig;
            String model = Models.api().getModel(object);
            if (StringUtils.isNotBlank(model)) {
                modelConfig = PamirsSession.getContext().getModelConfig(model);
            } else {
                modelConfig = null;
            }
            if (object instanceof D) {
                map = ((D) object).get_d();
            } else if (Map.class.isAssignableFrom(object.getClass())) {
                map = cast(object);
            } else {
                map = null;
            }
            if (map == null) {
                return;
            }
            if (ObjectHelper.isRepeat(repeatSet, map)) {
                return;
            }
            if (modelConfig == null) {
                convertMap(map, converter, repeatSet);
            } else {
                convertMap(map, modelConfig, converter, repeatSet);
            }
        }
    }

    /**
     * 无模型Map转换
     *
     * @param map       数据
     * @param converter 转换器
     */
    private static void convertMap(Map<String, Object> map, TimezoneConverter converter, Set<Object> repeatSet) {
        if (MapUtils.isEmpty(map)) {
            return;
        }
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof Date) {
                Object finalValue = converter.convertWithoutException(value);
                if (finalValue != null) {
                    entry.setValue(finalValue);
                }
            } else {
                convert(value, converter, repeatSet);
            }
        }
    }

    /**
     * 有模型Map转换
     *
     * @param map         数据
     * @param modelConfig 模型配置
     * @param converter   转换器
     */
    private static void convertMap(Map<String, Object> map, ModelConfig modelConfig, TimezoneConverter converter, Set<Object> repeatSet) {
        if (MapUtils.isEmpty(map)) {
            return;
        }
        List<ModelFieldConfig> modelFieldConfigs = modelConfig.getModelFieldConfigList();
        for (ModelFieldConfig modelFieldConfig : modelFieldConfigs) {
            String field = modelFieldConfig.getLname();
            Object value = map.get(field);
            if (value == null) {
                continue;
            }
            String ttype = modelFieldConfig.getTtype();
            if (TtypeEnum.isRelatedType(ttype)) {
                ttype = modelFieldConfig.getRelatedTtype();
            }
            if (TtypeEnum.isRelationType(ttype)) {
                convert(value, converter, repeatSet);
            } else {
                if (TtypeEnum.isDateType(ttype)) {
                    if (TimezoneConverter.isSupported(value)) {
                        map.put(field, converter.convert(value));
                    }
                }
            }
        }
    }

    /**
     * 集合转换
     *
     * @param collection 集合
     * @param converter  转换器
     */
    private static void convertList(Collection<Object> collection, TimezoneConverter converter, Set<Object> repeatSet) {
        if (CollectionUtils.isEmpty(collection)) {
            return;
        }
        for (Object item : collection) {
            if (item == null) {
                continue;
            }
            convert(item, converter, repeatSet);
        }
    }
}