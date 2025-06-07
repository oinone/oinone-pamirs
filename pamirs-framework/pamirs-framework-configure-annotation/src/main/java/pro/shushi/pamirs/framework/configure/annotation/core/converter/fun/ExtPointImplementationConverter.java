package pro.shushi.pamirs.framework.configure.annotation.core.converter.fun;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.ExtPoint;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelConverter;
import pro.shushi.pamirs.meta.api.dto.common.Message;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.ExecuteContext;
import pro.shushi.pamirs.meta.api.dto.meta.MetaNames;
import pro.shushi.pamirs.meta.domain.fun.ExtPointImplementation;
import pro.shushi.pamirs.meta.enmu.InformationLevelEnum;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;
import pro.shushi.pamirs.meta.util.ExtNamespaceAndNameUtils;
import pro.shushi.pamirs.meta.util.NamespaceAndFunUtils;
import pro.shushi.pamirs.meta.util.SystemSourceUtils;

import java.lang.reflect.Method;
import java.text.MessageFormat;

import static pro.shushi.pamirs.framework.configure.annotation.emnu.AnnotationExpEnumerate.BASE_EXT_POINT_INSTANCE_NO_IMPLEMENT_ERROR;
import static pro.shushi.pamirs.framework.configure.annotation.emnu.AnnotationExpEnumerate.BASE_EXT_POINT_INSTANCE_NO_NAMESPACE_ERROR;

/**
 * 扩展点实例注解转化器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
@SuppressWarnings({"rawtypes", "unused"})
@Slf4j
@Component
public class ExtPointImplementationConverter implements ModelConverter<ExtPointImplementation, Method> {

    @Override
    public int priority() {
        return 1;
    }

    @Override
    public Result validate(ExecuteContext context, MetaNames names, Method source) {
        // 可以在这里进行注解配置建议
        Result result = new Result();
        ExtPoint.Implement extPointImplementAnnotation = AnnotationUtils.getAnnotation(source, ExtPoint.Implement.class);
        if (null == extPointImplementAnnotation) {
            context.broken();
            return result.error();
        }
        ExtPoint extPointAnnotation = AnnotationUtils.findAnnotation(source, ExtPoint.class);
        ExtPoint.name extPointNameAnnotation = AnnotationUtils.findAnnotation(source, ExtPoint.name.class);
        if (null == extPointAnnotation && null == extPointNameAnnotation) {
            result.addMessage(new Message().setLevel(InformationLevelEnum.ERROR)
                    .error(BASE_EXT_POINT_INSTANCE_NO_IMPLEMENT_ERROR)
                    .append(MessageFormat
                            .format("扩展点实现需要实现配置了@ExtPoint或者@ExtPoint.name注解的扩展点接口，class:{0}，method:{1}",
                                    source.getDeclaringClass().getName(), source.getName())));
            context.error();
            result.error();
        }
        String namespace = ExtNamespaceAndNameUtils.namespace(source);
        if (StringUtils.isBlank(namespace)) {
            result.addMessage(new Message().setLevel(InformationLevelEnum.ERROR)
                    .error(BASE_EXT_POINT_INSTANCE_NO_NAMESPACE_ERROR)
                    .append(MessageFormat
                            .format("扩展点实现或扩展点需要通过@Ext注解指定命名空间，class:{0}，method:{1}",
                                    source.getDeclaringClass().getName(), source.getName())));
            context.error();
            result.error();
        }
        return result;
    }

    @Override
    public ExtPointImplementation convert(MetaNames names, Method source, ExtPointImplementation metaModelObjects) {
        ExtPoint.Implement extPointImplementAnnotation = AnnotationUtils.getAnnotation(source, ExtPoint.Implement.class);
        String namespace = ExtNamespaceAndNameUtils.namespace(source);
        String name = ExtNamespaceAndNameUtils.name(source);
        String executeNamespace = NamespaceAndFunUtils.namespace(source);
        String executeFun = NamespaceAndFunUtils.fun(source);
        SystemSourceEnum systemSource = SystemSourceUtils.fetch(source);
        assert extPointImplementAnnotation != null;
        metaModelObjects.setDisplayName(extPointImplementAnnotation.displayName())
                .setNamespace(namespace)
                .setName(name)
                .setExecuteNamespace(executeNamespace)
                .setExecuteFun(executeFun)
                .setDescription(extPointImplementAnnotation.summary())
                .setExpression(extPointImplementAnnotation.expression())
                .setPriority(extPointImplementAnnotation.priority())
                .setSystemSource(systemSource)
        ;
        return metaModelObjects;
    }

    @Override
    public String group() {
        return ExtPointImplementation.MODEL_MODEL;
    }

    @Override
    public Class<?> metaModelClazz() {
        return ExtPointImplementation.class;
    }

}
