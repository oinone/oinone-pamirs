package pro.shushi.pamirs.framework.configure.annotation.core.converter.fun;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelConverter;
import pro.shushi.pamirs.meta.api.core.faas.HookAfter;
import pro.shushi.pamirs.meta.api.core.faas.HookBefore;
import pro.shushi.pamirs.meta.api.dto.common.Message;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.ExecuteContext;
import pro.shushi.pamirs.meta.api.dto.meta.MetaNames;
import pro.shushi.pamirs.meta.domain.fun.Hook;
import pro.shushi.pamirs.meta.enmu.HookTypeEnum;
import pro.shushi.pamirs.meta.enmu.InformationLevelEnum;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;
import pro.shushi.pamirs.meta.util.NamespaceAndFunUtils;
import pro.shushi.pamirs.meta.util.SystemSourceUtils;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Optional;

import static pro.shushi.pamirs.framework.configure.annotation.emnu.AnnotationExpEnumerate.BASE_HOOK_BEFORE_NO_INTERFACE_ERROR;

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
public class HookConverter implements ModelConverter<Hook, Method> {

    @Override
    public int priority() {
        return 1;
    }

    @Override
    public Result validate(ExecuteContext context, MetaNames names, Method source) {
        // 可以在这里进行注解配置建议
        Result result = new Result();
        pro.shushi.pamirs.meta.annotation.Hook hookAnnotation = AnnotationUtils.getAnnotation(source, pro.shushi.pamirs.meta.annotation.Hook.class);
        if (null == hookAnnotation) {
            context.broken();
            return result.error();
        }
        if (!HookBefore.class.isAssignableFrom(source.getDeclaringClass()) && !HookAfter.class.isAssignableFrom(source.getDeclaringClass())) {
            result.addMessage(new Message().setLevel(InformationLevelEnum.ERROR)
                    .error(BASE_HOOK_BEFORE_NO_INTERFACE_ERROR)
                    .append(MessageFormat
                            .format("前置拦截器请实现HookBefore接口，后置拦截器请实现HookAfter接口，class:{0}，method:{1}",
                                    source.getDeclaringClass().getName(), source.getName())));
            context.error();
            result.error();
        }
        return result;
    }

    @Override
    public Hook convert(MetaNames names, Method source, Hook metaModelObject) {
        pro.shushi.pamirs.meta.annotation.Hook hookAnnotation = AnnotationUtils.getAnnotation(source, pro.shushi.pamirs.meta.annotation.Hook.class);
        String executeNamespace = NamespaceAndFunUtils.namespace(source);
        String executeFun = NamespaceAndFunUtils.fun(source);
        SystemSourceEnum systemSource = SystemSourceUtils.fetch(source);
        assert hookAnnotation != null;
        metaModelObject.setDisplayName(hookAnnotation.displayName())
                .setHookType(hookAnnotation.hookType())
                .setExecuteNamespace(executeNamespace)
                .setExecuteFun(executeFun)
                .setFunctionTypes(Optional.of(hookAnnotation.functionTypes()).filter(ArrayUtils::isNotEmpty).map(Lists::newArrayList).orElse(null))
                .setModule(Optional.of(hookAnnotation.module()).filter(ArrayUtils::isNotEmpty).map(Lists::newArrayList).orElse(null))
                .setModel(Optional.of(hookAnnotation.model()).filter(ArrayUtils::isNotEmpty).map(Lists::newArrayList).orElse(null))
                .setFun(Optional.of(hookAnnotation.fun()).filter(ArrayUtils::isNotEmpty).map(Lists::newArrayList).orElse(null))
                .setPriority(hookAnnotation.priority())
                .setDescription(hookAnnotation.description())
                .setActive(hookAnnotation.active())
                .setSystemSource(systemSource)
        ;
        if (HookAfter.class.isAssignableFrom(source.getDeclaringClass())) {
            metaModelObject.setHookType(HookTypeEnum.AFTER);
        }
        return metaModelObject;
    }


    @Override
    public String group() {
        return Hook.MODEL_MODEL;
    }

    @Override
    public Class<?> metaModelClazz() {
        return Hook.class;
    }

}
