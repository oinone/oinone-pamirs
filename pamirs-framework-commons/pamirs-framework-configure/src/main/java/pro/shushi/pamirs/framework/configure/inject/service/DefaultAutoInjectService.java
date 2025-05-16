package pro.shushi.pamirs.framework.configure.inject.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.common.api.MetaConfigureApi;
import pro.shushi.pamirs.framework.configure.inject.annotation.MetaField;
import pro.shushi.pamirs.framework.configure.inject.annotation.MetaFields;
import pro.shushi.pamirs.framework.configure.inject.api.AutoInjectService;
import pro.shushi.pamirs.framework.configure.inject.domain.MetaFieldConfig;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.meta.common.util.MapUtils;
import pro.shushi.pamirs.meta.util.FieldUtils;
import pro.shushi.pamirs.meta.util.JsonUtils;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * 自动注入服务（只支持json反序列化）
 * <p>
 * 2020/10/20 12:34 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Component
public class DefaultAutoInjectService implements AutoInjectService {

    private final static Map<String/*model*/, List<MetaFieldConfig>> injectFieldMap = new HashMap<>();

    /**
     * 数据字典自动注入配置示例：
     *
     * @MetaFields(model = DataDictionary.MODEL_MODEL, inject = {
     * @MetaField(from = "options", to = "itemList", isList = true, toClass = DataDictionaryItem.class)
     * })
     * @Component public class DataDictionaryInjectConfig implements MetaConfigureApi {
     * <p>
     * }
     */
    @SuppressWarnings("JavaDoc")
    @PostConstruct
    public void init() {
        Map<String, MetaConfigureApi> mapperMap = BeanDefinitionUtils.getBeansOfType(MetaConfigureApi.class);
        for (MetaConfigureApi mapperApi : Objects.requireNonNull(mapperMap).values()) {
            MetaFields metaFields = AnnotationUtils.findAnnotation(mapperApi.getClass(), MetaFields.class);
            if (null != metaFields) {
                String model = metaFields.model();
                MetaField[] metaFieldArray = metaFields.inject();
                if (!StringUtils.isBlank(model)) {
                    List<MetaFieldConfig> injectMap = MapUtils
                            .computeIfAbsent(injectFieldMap, model, k -> new ArrayList<>(metaFieldArray.length));
                    for (MetaField metaField : metaFieldArray) {
                        MetaFieldConfig metaFieldConfig = new MetaFieldConfig();
                        injectMap.add(metaFieldConfig.setFromFieldName(metaField.from())
                                .setToFieldName(metaField.to()).setToFieldClass(metaField.toClass())
                                .setList(metaField.isList()));
                    }
                }
            }
        }
    }

    @Override
    public void autoInject(String model, Object data) {
        List<MetaFieldConfig> injectList = injectFieldMap.get(model);
        if (null != injectList) {
            for (MetaFieldConfig metaFieldConfig : injectList) {
                Object fieldValue = FieldUtils.getFieldValue(data, metaFieldConfig.getFromFieldName());
                Object existFieldValue = FieldUtils.getFieldValue(data, metaFieldConfig.getToFieldName());
                if (null != fieldValue && null == existFieldValue) {
                    if (fieldValue instanceof String) {
                        if (metaFieldConfig.isList()) {
                            fieldValue = JsonUtils.parseObjectList((String) fieldValue, metaFieldConfig.getToFieldClass());
                        } else {
                            fieldValue = JsonUtils.parseObject((String) fieldValue, metaFieldConfig.getToFieldClass());
                        }
                    }
                    FieldUtils.setFieldValue(data, metaFieldConfig.getToFieldName(), fieldValue);
                }
            }
        }
    }

}
