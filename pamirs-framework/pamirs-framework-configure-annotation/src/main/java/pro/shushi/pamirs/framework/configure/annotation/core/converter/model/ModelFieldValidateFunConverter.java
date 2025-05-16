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
import pro.shushi.pamirs.meta.domain.fun.ComputeDefinition;
import pro.shushi.pamirs.meta.domain.fun.ExpressionDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.enmu.ComputeSceneEnum;

import java.lang.reflect.Field;
import java.util.ArrayList;
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
public class ModelFieldValidateFunConverter implements ModelConverter<Map<String, ComputeDefinition>, Field> {

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
        if (0 == validationAnnotation.check().length && 0 == validationAnnotation.checkWithTips().length) {
            return result.error();
        }
        return result;
    }

    @Override
    public Map<String, ComputeDefinition> convert(MetaNames names, Field source, Map<String, ComputeDefinition> computeDefinitionMap) {
        String module = names.getModule();
        @SuppressWarnings("unchecked")
        String sign = Spider.getExtension(ModelReflectSigner.class, ModelField.MODEL_MODEL).sign(names, source);
        Validation validationAnnotation = AnnotationUtils.getAnnotation(source, Validation.class);
        return Spider.getDefaultExtension(ValidationProcessor.class)
                .fetchComputeFunctionMap(computeDefinitionMap, validationAnnotation, module, ModelField.MODEL_MODEL, sign);
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
        Validation.Fun[] ruleAnnotations = validationAnnotation.checkWithTips();
        for (Validation.Fun fun : ruleAnnotations) {
            String check = fun.value();
            String sign = ExpressionDefinition.sign(type, ModelField.MODEL_MODEL, fieldSign, check);
            signs.add(sign);
        }
        String[] checks = validationAnnotation.check();
        for (String check : checks) {
            String sign = ExpressionDefinition.sign(type, ModelField.MODEL_MODEL, fieldSign, check);
            signs.add(sign);
        }
        return signs;
    }

}
