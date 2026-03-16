package pro.shushi.pamirs.framework.configure.annotation.core.converter.fun;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.locale.utils.I18nUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelConverter;
import pro.shushi.pamirs.meta.api.dto.common.Message;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.ExecuteContext;
import pro.shushi.pamirs.meta.api.dto.meta.MetaNames;
import pro.shushi.pamirs.meta.domain.fun.ExtPoint;
import pro.shushi.pamirs.meta.enmu.InformationLevelEnum;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;
import pro.shushi.pamirs.meta.util.ExtNamespaceAndNameUtils;
import pro.shushi.pamirs.meta.util.FunctionUtils;
import pro.shushi.pamirs.meta.util.SystemSourceUtils;

import java.lang.reflect.Method;
import java.text.MessageFormat;

import static pro.shushi.pamirs.framework.configure.annotation.emnu.AnnotationExpEnumerate.BASE_EXT_POINT_NO_FUN_NAME_ERROR;
import static pro.shushi.pamirs.framework.configure.annotation.emnu.AnnotationExpEnumerate.BASE_EXT_POINT_NO_INTERFACE_ERROR;

/**
 * 扩展点注解转化器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
@SuppressWarnings({"rawtypes", "unused"})
@Slf4j
@Component
public class ExtPointConverter implements ModelConverter<ExtPoint, Method> {

    @Override
    public int priority() {
        return 1;
    }

    @Override
    public Result validate(ExecuteContext context, MetaNames names, Method source) {
        // 可以在这里进行注解配置建议
        Result result = new Result();
        pro.shushi.pamirs.meta.annotation.ExtPoint extPointAnnotation = AnnotationUtils.getAnnotation(source, pro.shushi.pamirs.meta.annotation.ExtPoint.class);
        pro.shushi.pamirs.meta.annotation.ExtPoint.name extPointNameAnnotation = AnnotationUtils.getAnnotation(source, pro.shushi.pamirs.meta.annotation.ExtPoint.name.class);
        if (null == extPointAnnotation && null == extPointNameAnnotation) {
            context.broken();
            return result.error();
        }
        if (!source.getDeclaringClass().isInterface()) {
            result.addMessage(new Message().setLevel(InformationLevelEnum.ERROR)
                    .error(BASE_EXT_POINT_NO_INTERFACE_ERROR)
                    .append(MessageFormat
                            .format("请使用接口声明扩展点，class:{0}，method:{1}",
                                    source.getDeclaringClass().getName(), source.getName())));
            context.error();
            result.error();
        }
        if (null == extPointNameAnnotation) {
            result.addMessage(new Message().setLevel(InformationLevelEnum.INFO)
                    .msg(BASE_EXT_POINT_NO_FUN_NAME_ERROR)
                    .append(MessageFormat
                            .format("建议配置@ExtPoint注解的name属性，属性name用于指定扩展点名称，class:{0}，method:{1}",
                                    source.getDeclaringClass().getName(), source.getName())));
        }
        return result;
    }

    @Override
    public ExtPoint convert(MetaNames names, Method source, ExtPoint metaModelObject) {
        Class<?> extClazz = source.getDeclaringClass();
        String namespace = ExtNamespaceAndNameUtils.namespace(source);
        String name = ExtNamespaceAndNameUtils.name(source);
        SystemSourceEnum systemSource = SystemSourceUtils.fetch(source);
        pro.shushi.pamirs.meta.annotation.ExtPoint extPointAnnotation = AnnotationUtils.findAnnotation(source, pro.shushi.pamirs.meta.annotation.ExtPoint.class);
        assert extPointAnnotation != null;
        pro.shushi.pamirs.meta.annotation.ExtPoint.name extPointNameAnnotation = AnnotationUtils.getAnnotation(source, pro.shushi.pamirs.meta.annotation.ExtPoint.name.class);
        metaModelObject.setDisplayName(I18nUtils.translateExtPoint(names.getModule(), namespace, name, "displayName", StringUtils.defaultIfBlank(extPointAnnotation.displayName(), extClazz.getSimpleName())))
                .setNamespace(namespace)
                .setName(name)
                .setDescription(I18nUtils.translateExtPoint(names.getModule(), namespace, name, "description", StringUtils.defaultIfBlank(extPointAnnotation.summary(), null)))
                .setClazz(extClazz.getName())
                .setMethod(source.getName())
                .setArgumentList(FunctionUtils.convertArgumentList(source))
                .setReturnType(FunctionUtils.convertReturnType(source))
                .setSystemSource(systemSource)
        ;
        return metaModelObject;
    }

    @Override
    public String group() {
        return ExtPoint.MODEL_MODEL;
    }

    @Override
    public Class<?> metaModelClazz() {
        return ExtPoint.class;
    }

}
