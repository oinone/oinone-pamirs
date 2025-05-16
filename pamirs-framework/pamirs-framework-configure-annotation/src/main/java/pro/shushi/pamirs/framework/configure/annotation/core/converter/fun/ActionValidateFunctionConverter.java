package pro.shushi.pamirs.framework.configure.annotation.core.converter.fun;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.validation.Validation;
import pro.shushi.pamirs.meta.api.core.compute.systems.constraint.ValidationProcessor;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ConverterType;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelConverter;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelReflectSigner;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.ExecuteContext;
import pro.shushi.pamirs.meta.api.dto.meta.MetaNames;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.domain.fun.ComputeDefinition;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 校验函数注解转化器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
@Component
@SuppressWarnings({"rawtypes"})
public class ActionValidateFunctionConverter implements ModelConverter<Map<String, ComputeDefinition>, Method> {

    @Override
    public int priority() {
        return 2;
    }

    @Override
    public Result validate(ExecuteContext context, MetaNames names, Method source) {
        Result result = new Result();
        Validation validationAnnotation = AnnotationUtils.getAnnotation(source, Validation.class);
        if (null == validationAnnotation) {
            return result.error();
        }
        if (0 == validationAnnotation.check().length && 0 == validationAnnotation.checkWithTips().length) {
            return result.error();
        }
        return result;
    }

    @Override
    public Map<String, ComputeDefinition> convert(MetaNames names, Method source, Map<String, ComputeDefinition> computeFunctionMap) {
        String module = names.getModule();
        @SuppressWarnings("unchecked")
        String sign = Spider.getExtension(ModelReflectSigner.class, FunctionDefinition.MODEL_MODEL).sign(names, source);
        Validation validationAnnotation = AnnotationUtils.getAnnotation(source, Validation.class);
        return Spider.getDefaultExtension(ValidationProcessor.class)
                .fetchComputeFunctionMap(computeFunctionMap, validationAnnotation, module, FunctionDefinition.MODEL_MODEL, sign);
    }

    @Override
    public String group() {
        return ComputeDefinition.MODEL_MODEL;
    }

    @Override
    public Class<?> metaModelClazz() {
        return ComputeDefinition.class;
    }

    @Override
    public ConverterType type() {
        return ConverterType.map;
    }

    @Override
    public List<String> signs(MetaNames names, Method source) {
        String module = names.getModule();
        @SuppressWarnings("unchecked")
        String sign = Spider.getExtension(ModelReflectSigner.class, FunctionDefinition.MODEL_MODEL).sign(names, source);
        Validation validationAnnotation = AnnotationUtils.getAnnotation(source, Validation.class);
        return new ArrayList<>(Spider.getDefaultExtension(ValidationProcessor.class)
                .fetchComputeFunctionMap(new HashMap<>(), validationAnnotation, module, FunctionDefinition.MODEL_MODEL, sign)
                .keySet());
    }
}
