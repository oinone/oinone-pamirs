package pro.shushi.pamirs.framework.configure.annotation.core.converter.fun;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.locale.utils.I18nUtils;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.x.XService;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelConverter;
import pro.shushi.pamirs.meta.api.dto.common.Message;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.ExecuteContext;
import pro.shushi.pamirs.meta.api.dto.meta.MetaNames;
import pro.shushi.pamirs.meta.common.constants.FunctionDefaultsConstants;
import pro.shushi.pamirs.meta.common.constants.NamespaceConstants;
import pro.shushi.pamirs.meta.common.util.ListUtils;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.enmu.*;
import pro.shushi.pamirs.meta.util.FunctionUtils;
import pro.shushi.pamirs.meta.util.MethodUtils;
import pro.shushi.pamirs.meta.util.NamespaceAndFunUtils;
import pro.shushi.pamirs.meta.util.SystemSourceUtils;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.*;

import static pro.shushi.pamirs.framework.configure.annotation.emnu.AnnotationExpEnumerate.BASE_FUNCTION_INVALID_NAME_ERROR;
import static pro.shushi.pamirs.framework.configure.annotation.emnu.AnnotationExpEnumerate.BASE_FUNCTION_NO_FUN_ERROR;

/**
 * 函数注解转化器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
@SuppressWarnings({"rawtypes", "unused"})
@Component
public class FunctionConverter implements ModelConverter<FunctionDefinition, Method> {

    @Override
    public int priority() {
        return 0;
    }

    @Override
    public Result validate(ExecuteContext context, MetaNames names, Method source) {
        // 可以在这里进行注解配置建议
        if (MethodUtils.isInterface(source)) {
            return new Result().error();
        }
        return validateFunction(context, source);
    }

    public static Result validateFunction(ExecuteContext context, Method source) {
        Result result = new Result();
        Function functionAnnotation = AnnotationUtils.getAnnotation(source, Function.class);
        Function.fun funFunAnnotation = AnnotationUtils.getAnnotation(source, Function.fun.class);
        if (null != functionAnnotation || null != funFunAnnotation) {
            if (source.getName().startsWith("set") ||
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
        }
        Model modelAnnotation = AnnotationUtils.getAnnotation(source.getDeclaringClass(), Model.class);
        Model.model modelModelAnnotation = AnnotationUtils.getAnnotation(source.getDeclaringClass(), Model.model.class);
        Fun funAnnotation = AnnotationUtils.getAnnotation(source.getDeclaringClass(), Fun.class);
        if (null == functionAnnotation && null == funFunAnnotation) {
            result.error();
        }
        if (null == modelAnnotation && null == modelModelAnnotation && null == funAnnotation && null != functionAnnotation) {
            result.addMessage(new Message().setLevel(InformationLevelEnum.ERROR)
                    .error(BASE_FUNCTION_NO_FUN_ERROR)
                    .append(MessageFormat
                            .format("，class:{0}",
                                    source.getDeclaringClass().getName())));
            context.error();
            return result.error();
        }
        return result;
    }

    @Override
    public FunctionDefinition convert(MetaNames names, Method method, FunctionDefinition function) {
        Function functionAnnotation = AnnotationUtils.getAnnotation(method, Function.class);
        Function.Advanced functionAdvancedAnnotation = AnnotationUtils.getAnnotation(method, Function.Advanced.class);
        XService xServiceAnnotation = AnnotationUtils.getAnnotation(method, XService.class);
        function.setModule(names.getModule());
        String namespace = NamespaceAndFunUtils.namespace(method);
        String fun = NamespaceAndFunUtils.fun(method);
        String name = Optional.ofNullable(functionAnnotation).map(Function::name).filter(StringUtils::isNotBlank).orElse(null);
        if (StringUtils.isBlank(name) && NamespaceConstants.pamirs.equals(namespace)) {
            function.setName(Optional.ofNullable(fun).orElse(method.getName()));
        } else {
            function.setName(Optional.ofNullable(name).orElse(method.getName()));
        }
        NamespaceAndFunUtils.fillBeanName(method, function);
        SystemSourceEnum systemSource = SystemSourceUtils.fetch(method);
        String displayName = Optional.ofNullable(functionAdvancedAnnotation).map(Function.Advanced::displayName).filter(StringUtils::isNotBlank).orElse(null);
        FunctionTypeEnum[] type = Optional.ofNullable(functionAdvancedAnnotation).map(Function.Advanced::type).orElse(null);
        FunctionLanguageEnum language = Optional.ofNullable(functionAdvancedAnnotation).map(v -> v.language().name()).map(FunctionLanguageEnum::valueOf).orElse(FunctionLanguageEnum.JAVA);
        FunctionCategoryEnum category = Optional.ofNullable(functionAdvancedAnnotation).map(v -> v.category().name()).map(FunctionCategoryEnum::valueOf).orElse(null);
        List<FunctionSceneEnum> scenes = convertSceneFromEnumName(Optional.ofNullable(functionAnnotation).map(Function::scene).orElse(null));
        Boolean managed = Optional.ofNullable(functionAdvancedAnnotation).map(Function.Advanced::managed)
                .map(v -> v || null != function.getDataManager() && function.getDataManager()).orElse(false);
        FunctionOpenEnum[] openLevel = Optional.ofNullable(functionAnnotation).map(Function::openLevel).filter(ArrayUtils::isNotEmpty).orElse(new FunctionOpenEnum[]{FunctionOpenEnum.LOCAL, FunctionOpenEnum.REMOTE});
        Boolean isBuiltin = Optional.ofNullable(functionAdvancedAnnotation).map(Function.Advanced::builtin).orElse(null);
        Boolean check = Optional.ofNullable(functionAdvancedAnnotation).map(Function.Advanced::check).orElse(null);
        String description = Optional.ofNullable(functionAnnotation).map(Function::summary).orElse(null);
        String group = Optional.ofNullable(functionAdvancedAnnotation).map(Function.Advanced::group).orElse(null);
        String version = Optional.ofNullable(functionAdvancedAnnotation).map(Function.Advanced::version).orElse(null);
        int timeout = Optional.ofNullable(functionAdvancedAnnotation).map(Function.Advanced::timeout).orElse(FunctionDefaultsConstants.TIMEOUT);
        int retries = Optional.ofNullable(functionAdvancedAnnotation).map(Function.Advanced::retries).orElse(0);
        Boolean isLongPolling = Optional.ofNullable(functionAdvancedAnnotation).map(Function.Advanced::isLongPolling).orElse(null);
        String longPollingKey = Optional.ofNullable(functionAdvancedAnnotation).map(Function.Advanced::longPollingKey).orElse(null);
        Integer longPollingTimeout = Optional.ofNullable(functionAdvancedAnnotation).map(Function.Advanced::longPollingTimeout).orElse(null);
        Long bitOptions = FunctionBitOptions.DEFAULT_VALUE.getOption();
        function.setDisplayName(I18nUtils.translateFunction(names.getModule(), namespace, fun, "displayName", StringUtils.defaultIfBlank(displayName, null)))
                .setNamespace(namespace)
                .setFun(fun)
                .setType(ListUtils.toList(type))
                .setLanguage(language)
                .setCategory(category)
                .setScene(scenes)
                .setDataManager(managed)
                .setSource(FunctionSourceEnum.FUNCTION)
                .setOpenLevel(ListUtils.toList(openLevel))
                .setIsBuiltin(isBuiltin)
                .setDescription(I18nUtils.translateFunction(names.getModule(), namespace, fun, "description", StringUtils.defaultIfBlank(description, null)))
                .setGroup(group)
                .setVersion(version)
                .setTimeout(timeout)
                .setRetries(retries)
                .setIsLongPolling(isLongPolling)
                .setLongPollingKey(longPollingKey)
                .setLongPollingTimeout(longPollingTimeout)
                .setBitOptions(bitOptions)
                .setSystemSource(systemSource);
        function.setClazz(method.getDeclaringClass().getName())
                .setMethod(method.getName())
                .setArgumentList(FunctionUtils.convertArgumentList(method))
                .setReturnType(FunctionUtils.convertReturnType(method));
        if (function.getOpenLevel() != null) {
            if (!function.getOpenLevel().contains(FunctionOpenEnum.LOCAL)) {
                function.getOpenLevel().add(FunctionOpenEnum.LOCAL);
            }
            if (function.getOpenLevel().contains(FunctionOpenEnum.API)) {
                if (!function.getOpenLevel().contains(FunctionOpenEnum.REMOTE)) {
                    function.getOpenLevel().add(FunctionOpenEnum.REMOTE);
                }
            }
        }
        Optional.ofNullable(function.getOpenLevel()).ifPresent(v -> v.sort(Comparator.comparing(FunctionOpenEnum::value)));

        // 是否校验
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

    private List<FunctionSceneEnum> convertSceneFromEnumName(FunctionSceneEnum[] names) {
        if (null == names || 0 == names.length) {
            return null;
        }
        List<FunctionSceneEnum> enums = new ArrayList<>();
        Collections.addAll(enums, names);
        return enums;
    }

}
