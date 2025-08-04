package pro.shushi.pamirs.framework.configure.staticloader;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.common.emnu.FwExpEnumerate;
import pro.shushi.pamirs.framework.configure.MetaConfiguration;
import pro.shushi.pamirs.meta.api.core.compute.Prioritized;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelConverter;
import pro.shushi.pamirs.meta.api.core.configure.annotation.PrimaryFieldConverter;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.ExecuteContext;
import pro.shushi.pamirs.meta.api.dto.meta.MetaNames;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.meta.domain.model.ModelField;

import jakarta.annotation.Resource;
import java.lang.reflect.Field;
import java.util.*;

/**
 * 字段增强转换器
 * <p>
 * 2020/9/8 10:24 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Component
public class FieldEnhanceConverter {

    @Resource
    private MetaConfiguration metaConfiguration;

    public void convert(Field field, ModelField modelField) {
        MetaNames names = new MetaNames();
        ExecuteContext executeContext = new ExecuteContext();
        Map<String, PrimaryFieldConverter> converterMap = BeanDefinitionUtils.getBeansOfType(PrimaryFieldConverter.class);
        List<PrimaryFieldConverter> converterList = new ArrayList<>(Objects.requireNonNull(converterMap).values());
        converterList.sort(Comparator.comparingInt(Prioritized::priority));
        for (PrimaryFieldConverter primaryFieldConverter : converterList) {
            @SuppressWarnings("unchecked")
            ModelConverter<ModelField, Field> modelConverter = ((ModelConverter<ModelField, Field>) primaryFieldConverter);
            @SuppressWarnings("rawtypes") Result validateResult = modelConverter.validate(executeContext, names, field);
            if (executeContext.isBroken()) {
                validateResult.logMessages(metaConfiguration.getLogLevel());
                throw PamirsException.construct(FwExpEnumerate.BASE_FIELD_ENHANCE_CONVERTER_ERROR)
                        .appendMsg(("field:" + modelField.getField())).errThrow();
            }
            if (!validateResult.isSuccess()) {
                continue;
            }
            modelConverter.convert(names, field, modelField);
        }
    }

}
