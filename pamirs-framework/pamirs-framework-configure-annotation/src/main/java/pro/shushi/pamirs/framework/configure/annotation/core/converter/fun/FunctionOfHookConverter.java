package pro.shushi.pamirs.framework.configure.annotation.core.converter.fun;

import com.google.common.collect.Lists;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.locale.utils.I18nUtils;
import pro.shushi.pamirs.meta.annotation.Hook;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelConverter;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.ExecuteContext;
import pro.shushi.pamirs.meta.api.dto.meta.MetaNames;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.enmu.*;
import pro.shushi.pamirs.meta.util.FunctionUtils;
import pro.shushi.pamirs.meta.util.NamespaceAndFunUtils;
import pro.shushi.pamirs.meta.util.SystemSourceUtils;

import java.lang.reflect.Method;

/**
 * 拦截器函数注解转化器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
@SuppressWarnings({"rawtypes", "unused"})
@Slf4j
@Component
public class FunctionOfHookConverter implements ModelConverter<FunctionDefinition, Method> {

    @Override
    public int priority() {
        return 1;
    }

    @Override
    public Result validate(ExecuteContext context, MetaNames names, Method source) {
        // 可以在这里进行注解配置建议
        Result result = new Result();
        Hook hookAnnotation = AnnotationUtils.getAnnotation(source, Hook.class);
        if (null == hookAnnotation) {
            result.error();
        }
        return result;
    }

    @Override
    public FunctionDefinition convert(MetaNames names, Method method, FunctionDefinition function) {
        Hook hookAnnotation = AnnotationUtils.getAnnotation(method, Hook.class);
        String namespace = NamespaceAndFunUtils.namespace(method);
        String fun = NamespaceAndFunUtils.fun(method);
        NamespaceAndFunUtils.fillBeanName(method, function);
        SystemSourceEnum systemSource = SystemSourceUtils.fetch(method);
        assert hookAnnotation != null;
        function.setDisplayName(I18nUtils.translateHook(names.getModule(), namespace, fun, "displayName", hookAnnotation.displayName()))
                .setModule(names.getModule())
                .setNamespace(namespace)
                .setFun(fun)
                .setName(method.getName())
                .setType(Lists.newArrayList(FunctionTypeEnum.QUERY))
                .setLanguage(FunctionLanguageEnum.JAVA)
                .setCategory(FunctionCategoryEnum.OTHER)
                .setSource(FunctionSourceEnum.HOOK)
                .setOpenLevel(Lists.newArrayList(FunctionOpenEnum.REMOTE))
                .setDataManager(false)
                .setDescription(I18nUtils.translateHook(names.getModule(), namespace, fun, "summary", hookAnnotation.description()))
                .setClazz(method.getDeclaringClass().getName())
                .setMethod(method.getName())
                .setArgumentList(FunctionUtils.convertArgumentList(method))
                .setReturnType(FunctionUtils.convertReturnType(method))
                .setSystemSource(systemSource)
        ;
        return function;
    }

    @Override
    public String group() {
        return FunctionDefinition.MODEL_MODEL;
    }

    @Override
    public Class<?> metaModelClazz() {
        return FunctionDefinition.class;
    }

}
