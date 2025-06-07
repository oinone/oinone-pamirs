package pro.shushi.pamirs.framework.configure.annotation.core.converter.fun;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelConverter;
import pro.shushi.pamirs.meta.api.dto.common.Message;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.ExecuteContext;
import pro.shushi.pamirs.meta.api.dto.meta.MetaNames;
import pro.shushi.pamirs.meta.common.util.ListUtils;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.enmu.*;
import pro.shushi.pamirs.meta.util.FunctionUtils;
import pro.shushi.pamirs.meta.util.NamespaceAndFunUtils;
import pro.shushi.pamirs.meta.util.SystemSourceUtils;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static pro.shushi.pamirs.framework.configure.annotation.emnu.AnnotationExpEnumerate.BASE_FUNCTION_INVALID_NAME_ERROR;

/**
 * 动作函数注解转化器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
@SuppressWarnings({"rawtypes", "unused"})
@Slf4j
@Component
public class FunctionOfActionConverter implements ModelConverter<FunctionDefinition, Method> {

    @Override
    public int priority() {
        return 1;
    }

    @Override
    public Result validate(ExecuteContext context, MetaNames names, Method source) {
        // 可以在这里进行注解配置建议
        Result result = new Result();
        Action actionAnnotation = AnnotationUtils.getAnnotation(source, Action.class);
        if (null == actionAnnotation) {
            result.error();
        } else if (source.getName().startsWith("set") ||
                source.getName().startsWith("get") ||
                source.getName().startsWith("unset") ||
                "toString".equals(source.getName())) {
            result.addMessage(new Message().setLevel(InformationLevelEnum.ERROR)
                    .error(BASE_FUNCTION_INVALID_NAME_ERROR)
                    .append(MessageFormat
                            .format("class:{0}，method:{1}",
                                    source.getDeclaringClass().getName(), source.getName())));
            context.broken().error();
            return result.error();
        }
        return result;
    }

    @Override
    public FunctionDefinition convert(MetaNames names, Method method, FunctionDefinition function) {
        Action actionAnnotation = AnnotationUtils.getAnnotation(method, Action.class);
        Action.Advanced actionAdvancedAnnotation = AnnotationUtils.getAnnotation(method, Action.Advanced.class);
        String namespace = NamespaceAndFunUtils.namespace(method);
        String fun = NamespaceAndFunUtils.fun(method);
        NamespaceAndFunUtils.fillBeanName(method, function);
        SystemSourceEnum systemSource = SystemSourceUtils.fetch(method);
        Boolean managed = Optional.ofNullable(actionAdvancedAnnotation).map(Action.Advanced::managed)
                .map(v -> v || null != function.getDataManager() && function.getDataManager()).orElse(false);
        function.setDisplayName(Optional.ofNullable(actionAnnotation).map(Action::displayName).orElse(fun))
                .setModule(names.getModule())
                .setNamespace(namespace)
                .setFun(fun)
                .setName(Optional.ofNullable(actionAdvancedAnnotation).map(Action.Advanced::name).filter(StringUtils::isNotBlank).orElse(method.getName()))
                .setType(Optional.ofNullable(actionAdvancedAnnotation).map(Action.Advanced::type).map(ListUtils::<FunctionTypeEnum>toList).orElse(Lists.newArrayList(FunctionTypeEnum.UPDATE)))
                .setLanguage(FunctionLanguageEnum.JAVA)
                .setCategory(FunctionCategoryEnum.OTHER)
                .setSource(FunctionSourceEnum.ACTION)
                .setDataManager(managed)
                .setDescription(Optional.ofNullable(actionAnnotation).map(Action::summary).orElse(null))
                .setClazz(method.getDeclaringClass().getName())
                .setMethod(method.getName())
                .setArgumentList(FunctionUtils.convertArgumentList(method))
                .setReturnType(FunctionUtils.convertReturnType(method))
                .setSystemSource(systemSource);
        if (managed || CollectionUtils.isEmpty(function.getOpenLevel())) {
            function.setOpenLevel(Lists.newArrayList(FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE, FunctionOpenEnum.API));
        } else {
            List<FunctionOpenEnum> openLevel = function.getOpenLevel();
            if (!openLevel.contains(FunctionOpenEnum.LOCAL)) {
                openLevel.add(FunctionOpenEnum.LOCAL);
            }
            if (!openLevel.contains(FunctionOpenEnum.REMOTE)) {
                openLevel.add(FunctionOpenEnum.REMOTE);
            }
            if (!openLevel.contains(FunctionOpenEnum.API)) {
                openLevel.add(FunctionOpenEnum.API);
            }
            function.setOpenLevel(openLevel);
        }
        Optional.ofNullable(function.getOpenLevel()).ifPresent(v -> v.sort(Comparator.comparing(FunctionOpenEnum::value)));

        // 是否校验
        Boolean check = Optional.ofNullable(actionAdvancedAnnotation).map(Action.Advanced::check).orElse(null);
        if (null != check && check) {
            function.enableBitOption(FunctionBitOptions.ENABLE_CHECK.getOption());
        } else {
            function.disableBitOption(FunctionBitOptions.ENABLE_CHECK.getOption());
        }

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
