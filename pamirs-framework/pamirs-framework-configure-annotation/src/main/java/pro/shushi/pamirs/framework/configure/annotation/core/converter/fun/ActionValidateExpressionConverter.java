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
import pro.shushi.pamirs.meta.domain.fun.ExpressionDefinition;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 校验表达式注解转化器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
@Component
@SuppressWarnings({"rawtypes"})
public class ActionValidateExpressionConverter implements ModelConverter<Map<String, ExpressionDefinition>, Method> {

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
        if (0 == validationAnnotation.rule().length && 0 == validationAnnotation.ruleWithTips().length) {
            return result.error();
        }
        return result;
    }

    @Override
    public Map<String, ExpressionDefinition> convert(MetaNames names, Method source, Map<String, ExpressionDefinition> expressionMap) {
        String module = names.getModule();
        @SuppressWarnings("unchecked")
        String sign = Spider.getExtension(ModelReflectSigner.class, FunctionDefinition.MODEL_MODEL).sign(names, source);
        Validation validationAnnotation = AnnotationUtils.getAnnotation(source, Validation.class);
        return Spider.getDefaultExtension(ValidationProcessor.class)
                .fetchExpressionMap(expressionMap, validationAnnotation, module, FunctionDefinition.MODEL_MODEL, sign);
    }

    @Override
    public String group() {
        return ExpressionDefinition.MODEL_MODEL;
    }

    @Override
    public Class<?> metaModelClazz() {
        return ExpressionDefinition.class;
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
                .fetchExpressionMap(new HashMap<>(), validationAnnotation, module, FunctionDefinition.MODEL_MODEL, sign).keySet());
    }
}
