package pro.shushi.pamirs.framework.configure.annotation.core.converter.fun;

import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.locale.utils.I18nUtils;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelConverter;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.ExecuteContext;
import pro.shushi.pamirs.meta.api.dto.meta.MetaNames;
import pro.shushi.pamirs.meta.common.constants.FunctionDefaultsConstants;
import pro.shushi.pamirs.meta.domain.fun.InterfaceDefinition;
import pro.shushi.pamirs.meta.util.MethodUtils;
import pro.shushi.pamirs.meta.util.NamespaceAndFunUtils;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * 接口注解转化器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
@SuppressWarnings({"rawtypes", "unused"})
@Component
public class InterfaceConverter implements ModelConverter<InterfaceDefinition, Method> {

    @Resource
    private FunctionConverter functionConverter;

    @Override
    public int priority() {
        return 0;
    }

    @Override
    public Result validate(ExecuteContext context, MetaNames names, Method source) {
        // 可以在这里进行注解配置建议
        if (!MethodUtils.isInterface(source)) {
            return new Result().error();
        }
        return FunctionConverter.validateFunction(context, source);
    }

    @Override
    public InterfaceDefinition convert(MetaNames names, Method method, InterfaceDefinition function) {
        function = (InterfaceDefinition) functionConverter.convert(names, method, function);

        String namespace = NamespaceAndFunUtils.namespace(method);
        String fun = NamespaceAndFunUtils.fun(method);
        Function functionAnnotation = AnnotationUtils.getAnnotation(method, Function.class);
        Function.Advanced functionAdvancedAnnotation = AnnotationUtils.getAnnotation(method, Function.Advanced.class);

        String displayName = Optional.ofNullable(functionAdvancedAnnotation).map(Function.Advanced::displayName).filter(StringUtils::isNotBlank).orElse(null);
        String description = Optional.ofNullable(functionAnnotation).map(Function::summary).orElse(null);

        function.setDisplayName(I18nUtils.translateInterfaces(names.getModule(), namespace, fun, "displayName", displayName));
        function.setDescription(I18nUtils.translateInterfaces(names.getModule(), namespace, fun, "description", description));
        function.setOpenLevel(null);
        function.setGroup(Optional.ofNullable(function.getGroup()).orElse(FunctionDefaultsConstants.GROUP));
        function.setVersion(Optional.ofNullable(function.getVersion()).orElse(FunctionDefaultsConstants.VERSION));
        function.setTimeout(Optional.ofNullable(function.getTimeout()).orElse(FunctionDefaultsConstants.TIMEOUT));
        return function;
    }

    @Override
    public String group() {
        return InterfaceDefinition.MODEL_MODEL;
    }

    @Override
    public Class<?> metaModelClazz() {
        return InterfaceDefinition.class;
    }

}
