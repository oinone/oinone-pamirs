package pro.shushi.pamirs.framework.configure.annotation.core.converter.model;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.configure.contants.NameConstants;
import pro.shushi.pamirs.locale.utils.I18nUtils;
import pro.shushi.pamirs.meta.annotation.Errors;
import pro.shushi.pamirs.meta.api.core.compute.systems.enmu.ErrorsProcessor;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ConverterType;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelConverter;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.ExecuteContext;
import pro.shushi.pamirs.meta.api.dto.meta.MetaNames;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.enmu.Enums;
import pro.shushi.pamirs.meta.common.enmu.IEnum;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.domain.model.ErrorDefinition;
import pro.shushi.pamirs.meta.util.TypeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 异常错误注解转化器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
@Component
@SuppressWarnings({"rawtypes"})
public class ErrorDefinitionConverter implements ModelConverter<Map<String, ErrorDefinition>, Class> {

    @Override
    public int priority() {
        return 1;
    }

    @Override
    public Result validate(ExecuteContext context, MetaNames names, Class source) {
        Result result = new Result();
        Errors errorAnnotation = AnnotationUtils.getAnnotation(source, Errors.class);
        if (null == errorAnnotation || !TypeUtils.isIEnumClass(source)) {
            context.broken();
            return result.error();
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, ErrorDefinition> convert(MetaNames names, Class source, Map<String, ErrorDefinition> errorMap) {
        String module = names.getModule();
        return Spider.getDefaultExtension(ErrorsProcessor.class).fetchErrorDefinitionMap(errorMap, module, source);
    }

    @Override
    public String group() {
        return ErrorDefinition.MODEL_MODEL;
    }

    @Override
    public Class<?> metaModelClazz() {
        return ErrorDefinition.class;
    }

    @Override
    public ConverterType type() {
        return ConverterType.map;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> signs(MetaNames names, Class source) {
        List<String> signs = new ArrayList<>();
        String errorClazz = source.getName();
        List<IEnum> enums = Enums.getEnumList((Class<IEnum>) source);
        for (IEnum one : enums) {
            String errorSign = errorClazz + CharacterConstants.SEPARATOR_DOT + one.name();
            signs.add(errorSign);
        }
        return signs;
    }

}
