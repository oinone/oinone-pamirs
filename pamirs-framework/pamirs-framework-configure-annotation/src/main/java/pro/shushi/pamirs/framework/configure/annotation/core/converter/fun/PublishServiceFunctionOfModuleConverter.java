package pro.shushi.pamirs.framework.configure.annotation.core.converter.fun;

import com.google.common.collect.Lists;
import org.apache.dubbo.common.utils.MethodUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.configure.annotation.emnu.AnnotationExpEnumerate;
import pro.shushi.pamirs.meta.annotation.Module;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelConverter;
import pro.shushi.pamirs.meta.api.dto.common.Message;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.ExecuteContext;
import pro.shushi.pamirs.meta.api.dto.meta.MetaNames;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.meta.constant.ModuleFunctionConstants;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.enmu.*;
import pro.shushi.pamirs.meta.util.FunctionUtils;
import pro.shushi.pamirs.meta.util.NamespaceAndFunUtils;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.List;

/**
 * "函数动态发布成远程服务"的Function定义
 * @author cpc@shushi.pro
 * @version 1.0.0
 * date 2024/1/18
 */
@SuppressWarnings({"rawtypes", "unused"})
@Slf4j
@Component
public class PublishServiceFunctionOfModuleConverter implements ModelConverter<FunctionDefinition, Class>, ModuleFunctionConstants {



    @Override
    public int priority() {
        return 2;
    }


    @Override
    public String group() {
        return FunctionDefinition.MODEL_MODEL;
    }

    @Override
    public Result validate(ExecuteContext context, MetaNames names, Class source) {
        Module moduleAnnotation = AnnotationUtils.getAnnotation(source, Module.class);
        Module.module moduleModuleAnnotation = AnnotationUtils.getAnnotation(source, Module.module.class);
        Module.Advanced moduleAdvancedAnnotation = AnnotationUtils.getAnnotation(source, Module.Advanced.class);
        Result result = new Result();

        if (null == moduleAnnotation) {
            if (null != moduleModuleAnnotation || null != moduleAdvancedAnnotation) {
                result.addMessage(new Message().setLevel(InformationLevelEnum.ERROR)
                        .error(AnnotationExpEnumerate.BASE_MODULE_NO_MODULE_ERROR)
                        .append(MessageFormat
                                .format("请为模块类配置@Module，否则系统会忽略该模块，class:{0}",
                                        source.getName())));
                context.error();
            }
            context.broken();
            return result.error();
        }
        return result;
    }

    @Override
    public FunctionDefinition convert(MetaNames names, Class source, FunctionDefinition function) {
        Module.module module = AnnotationUtils.getAnnotation(source, Module.module.class);
        String namespace = module.value();
        String fun = FUN_PUBLISH_SERVICE;
        Object object= BeanDefinitionUtils.getBean(BEAN_MODEL_FUNCTION_PUBLISH);
        Method method = MethodUtils.findMethod(object.getClass(),fun,List.class);
        NamespaceAndFunUtils.fillBeanName(method, function);
        function.setDisplayName(fun)
                .setModule(names.getModule())
                .setNamespace(namespace)
                .setFun(fun)
                .setName(method.getName())
                .setType(Lists.newArrayList(FunctionTypeEnum.UPDATE))
                .setLanguage(FunctionLanguageEnum.JAVA)
                .setCategory(FunctionCategoryEnum.OTHER)
                .setSource(FunctionSourceEnum.FUNCTION)
                .setDataManager(false)
                .setDescription(null)
                .setClazz(method.getDeclaringClass().getName())
                .setMethod(method.getName())
                .setArgumentList(FunctionUtils.convertArgumentList(method))
                .setReturnType(FunctionUtils.convertReturnType(method))
                .setSystemSource(SystemSourceEnum.BASE);
        function.setOpenLevel(Lists.newArrayList(FunctionOpenEnum.LOCAL,  FunctionOpenEnum.REMOTE));
        return function;
    }
    public String sign(MetaNames names, Class source) {
        Module.module module = AnnotationUtils.getAnnotation(source, Module.module.class);
        String namespace = module.value();
        String fun = FUN_PUBLISH_SERVICE;
        return namespace + CharacterConstants.SEPARATOR_DOT + fun;
    }

    @Override
    public Class<?> metaModelClazz() {
        return FunctionDefinition.class;
    }

}
