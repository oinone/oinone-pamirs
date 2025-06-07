package pro.shushi.pamirs.framework.configure.annotation.core.converter.model;

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
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.enmu.ComputeSceneEnum;

import java.lang.reflect.Field;
import java.util.ArrayList;
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
public class ModelFieldValidateExpressionConverter implements ModelConverter<Map<String, ExpressionDefinition>, Field> {

    @Override
    public int priority() {
        return 200;
    }

    @Override
    public Result validate(ExecuteContext context, MetaNames names, Field source) {
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
    public Map<String, ExpressionDefinition> convert(MetaNames names, Field source, Map<String, ExpressionDefinition> expressionMap) {
        String module = names.getModule();
        @SuppressWarnings("unchecked")
        String sign = Spider.getExtension(ModelReflectSigner.class, ModelField.MODEL_MODEL).sign(names, source);
        Validation validationAnnotation = AnnotationUtils.getAnnotation(source, Validation.class);
        return Spider.getDefaultExtension(ValidationProcessor.class)
                .fetchExpressionMap(expressionMap, validationAnnotation, module, ModelField.MODEL_MODEL, sign);
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

    @SuppressWarnings("unchecked")
    @Override
    public List<String> signs(MetaNames names, Field source) {
        List<String> signs = new ArrayList<>();
        String fieldSign = Spider.getExtension(ModelReflectSigner.class, ModelField.MODEL_MODEL).sign(names, source);
        Validation validationAnnotation = AnnotationUtils.getAnnotation(source, Validation.class);
        if (null == validationAnnotation) {
            return null;
        }
        ComputeSceneEnum type = ComputeSceneEnum.VALIDATE;
        Validation.Rule[] ruleAnnotations = validationAnnotation.ruleWithTips();
        for (Validation.Rule rule : ruleAnnotations) {
            String expression = rule.value();
            String sign = ExpressionDefinition.sign(type, ModelField.MODEL_MODEL, fieldSign, expression);
            signs.add(sign);
        }
        String[] rules = validationAnnotation.rule();
        for (String rule : rules) {
            String sign = ExpressionDefinition.sign(type, ModelField.MODEL_MODEL, fieldSign, rule);
            signs.add(sign);
        }
        return signs;
    }

}
