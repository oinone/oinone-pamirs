package pro.shushi.pamirs.framework.configure.annotation.core.converter.fun;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.ExtPoint;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelConverter;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.ExecuteContext;
import pro.shushi.pamirs.meta.api.dto.meta.MetaNames;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.enmu.FunctionCategoryEnum;
import pro.shushi.pamirs.meta.enmu.FunctionLanguageEnum;
import pro.shushi.pamirs.meta.enmu.FunctionSourceEnum;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;
import pro.shushi.pamirs.meta.util.ExtNamespaceAndNameUtils;
import pro.shushi.pamirs.meta.util.FunctionUtils;
import pro.shushi.pamirs.meta.util.NamespaceAndFunUtils;
import pro.shushi.pamirs.meta.util.SystemSourceUtils;

import java.lang.reflect.Method;

/**
 * 扩展点函数注解转化器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
@SuppressWarnings({"rawtypes", "unused"})
@Slf4j
@Component
public class FunctionOfExtPointConverter implements ModelConverter<FunctionDefinition, Method> {

    @Override
    public int priority() {
        return 1;
    }

    @Override
    public Result validate(ExecuteContext context, MetaNames names, Method source) {
        // 可以在这里进行注解配置建议
        Result result = new Result();
        ExtPoint extPointAnnotation = AnnotationUtils.getAnnotation(source, ExtPoint.class);
        if (null == extPointAnnotation) {
            return result.error();
        }
        return result;
    }

    @Override
    public FunctionDefinition convert(MetaNames names, Method method, FunctionDefinition function) {
        ExtPoint extPointAnnotation = AnnotationUtils.getAnnotation(method, ExtPoint.class);
        String namespace = ExtNamespaceAndNameUtils.namespace(method);
        String name = ExtNamespaceAndNameUtils.name(method);
        NamespaceAndFunUtils.fillBeanName(method, function);
        SystemSourceEnum systemSource = SystemSourceUtils.fetch(method);
        assert extPointAnnotation != null;
        function.setDisplayName(extPointAnnotation.displayName())
                .setModule(names.getModule())
                .setNamespace(namespace)
                .setFun(name)
                .setName(name)
                .setLanguage(FunctionLanguageEnum.JAVA)
                .setCategory(FunctionCategoryEnum.OTHER)
                .setSource(FunctionSourceEnum.EXTPOINT)
                .setOpenLevel(null)
                .setDataManager(false)
                .setDescription(extPointAnnotation.summary())
                .setClazz(method.getDeclaringClass().getName())
                .setMethod(method.getName())
                .setArgumentList(FunctionUtils.convertArgumentList(method))
                .setReturnType(FunctionUtils.convertReturnType(method))
                .setSystemSource(systemSource)
        ;
        return function;
    }

    @Override
    public String sign(MetaNames names, Method source) {
        String namespace = ExtNamespaceAndNameUtils.namespace(source);
        String fun = ExtNamespaceAndNameUtils.name(source);
        return namespace + CharacterConstants.SEPARATOR_DOT + fun;
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
