package pro.shushi.pamirs.framework.configure.annotation.core.converter.model;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.Errors;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.api.core.compute.systems.enmu.ErrorsProcessor;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelConverter;
import pro.shushi.pamirs.meta.api.dto.common.Message;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.ExecuteContext;
import pro.shushi.pamirs.meta.api.dto.meta.MetaNames;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.domain.model.ErrorsDefinition;
import pro.shushi.pamirs.meta.enmu.InformationLevelEnum;
import pro.shushi.pamirs.meta.util.TypeUtils;

import java.text.MessageFormat;

import static pro.shushi.pamirs.framework.configure.annotation.emnu.AnnotationExpEnumerate.BASE_ERRORS_HAS_FUN_ERROR;

/**
 * 异常错误组注解转化器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
@Component
@SuppressWarnings({"rawtypes", "unused"})
public class ErrorsConverter implements ModelConverter<ErrorsDefinition, Class> {

    @Override
    public int priority() {
        return 1;
    }

    @Override
    @SuppressWarnings({"rawtypes"})
    public Result validate(ExecuteContext context, MetaNames names, Class source) {
        Result result = new Result();
        Errors errorAnnotation = AnnotationUtils.getAnnotation(source, Errors.class);
        if (null == errorAnnotation || !TypeUtils.isIEnumClass(source)) {
            context.broken();
            return result.error();
        }
        {
            Fun funAnnotation = AnnotationUtils.getAnnotation(source, Fun.class);
            if (null != funAnnotation) {
                result.addMessage(new Message().setLevel(InformationLevelEnum.ERROR)
                        .error(BASE_ERRORS_HAS_FUN_ERROR)
                        .append(MessageFormat
                                .format("请不要在错误枚举类上配置@Fun注解，class:{0}",
                                        source.getName())));
                context.error().broken();
                return result.error();
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ErrorsDefinition convert(MetaNames names, Class source, ErrorsDefinition errorsDefinition) {
        String module = names.getModule();
        Spider.getDefaultExtension(ErrorsProcessor.class).fillErrorsFromEnum(errorsDefinition, module, source);
        return errorsDefinition;
    }

    @Override
    public String group() {
        return ErrorsDefinition.MODEL_MODEL;
    }

    @Override
    public Class<?> metaModelClazz() {
        return ErrorsDefinition.class;
    }

}
