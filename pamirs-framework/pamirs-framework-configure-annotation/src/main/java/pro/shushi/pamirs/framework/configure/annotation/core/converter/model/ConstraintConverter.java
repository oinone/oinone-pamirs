package pro.shushi.pamirs.framework.configure.annotation.core.converter.model;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.locale.utils.I18nUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ConverterType;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelConverter;
import pro.shushi.pamirs.meta.api.dto.common.Message;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.ExecuteContext;
import pro.shushi.pamirs.meta.api.dto.meta.MetaNames;
import pro.shushi.pamirs.meta.base.Empty;
import pro.shushi.pamirs.meta.common.util.ListUtils;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.enmu.InformationLevelEnum;
import pro.shushi.pamirs.meta.enmu.OnCascadeEnum;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

import java.util.*;

import static pro.shushi.pamirs.framework.configure.annotation.emnu.AnnotationExpEnumerate.BASE_CONSTRAINT_NO_REFERENCE_ERROR;

/**
 * 模型逻辑外键约束注解转化器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
@Slf4j
@Component
@SuppressWarnings({"rawtypes", "unused"})
public class ConstraintConverter implements ModelConverter<Map<String, ModelField>, Class> {

    @Override
    public int priority() {
        return 1;
    }

    @Override
    @SuppressWarnings({"rawtypes"})
    public Result validate(ExecuteContext context, MetaNames names, Class source) {
        Result result = new Result();
        Model.Constraints constraintsAnnotation = AnnotationUtils.getAnnotation(source, Model.Constraints.class);
        if (null == constraintsAnnotation || ArrayUtils.isEmpty(constraintsAnnotation.value())) {
            context.broken();
            return result.error();
        }
        for (Model.Constraint constraint : constraintsAnnotation.value()) {
            if (StringUtils.isBlank(constraint.references()) && Empty.class.equals(constraint.referenceClass())) {
                context.broken();
                return result.error();
            }
            String references = Optional.of(constraint.references())
                    .orElse(Optional.of(constraint.referenceClass()).map(Models.api()::getModel).orElse(null));
            if (StringUtils.isBlank(references)) {
                result.addMessage(new Message().setLevel(InformationLevelEnum.ERROR)
                        .error(BASE_CONSTRAINT_NO_REFERENCE_ERROR)
                        .append(I18nUtils.translate("ConstraintConverter.referencesMissing", names.getModel())));
                result.error();
                context.error().broken();
            }
        }
        return result;
    }

    @Override
    public Map<String, ModelField> convert(MetaNames names, Class source, Map<String, ModelField> metaModelObject) {
        Map<String, ModelField> dataMap = new HashMap<>();
        Model.Constraints constraintsAnnotation = AnnotationUtils.getAnnotation(source, Model.Constraints.class);
        for (Model.Constraint constraintAnnotation : Objects.requireNonNull(constraintsAnnotation).value()) {
            String model = names.getModel();
            String key = ModelField.sign(names.getModel(), constraintAnnotation.foreignKey());
            ModelField relation = Optional.ofNullable(metaModelObject.get(key)).orElse(new ModelField());
            relation.setLname(constraintAnnotation.foreignKey())
                    .setDisplayName(constraintAnnotation.foreignKey())
                    .setLtype(HashMap.class.getName())
                    .setTtype(constraintAnnotation.unique() ? TtypeEnum.O2O : TtypeEnum.M2O)
                    .setModel(model)
                    .setField(constraintAnnotation.foreignKey())
                    .setName(constraintAnnotation.foreignKey())
                    .setPageSize(constraintAnnotation.pageSize())
                    .setDomain(Optional.of(constraintAnnotation.domain()).filter(StringUtils::isNotBlank).orElse(null))
                    .setDomainSize(constraintAnnotation.domainSize())
                    .setOnDelete(OnCascadeEnum.valueOf(constraintAnnotation.onDelete().name()))
                    .setOnUpdate(OnCascadeEnum.valueOf(constraintAnnotation.onUpdate().name()))
                    .setReferenceFields(ListUtils.toList(constraintAnnotation.referenceFields()))
                    .setReferences(Optional.of(constraintAnnotation.references()).orElse(Optional.of(constraintAnnotation.referenceClass()).map(Models.api()::getModel).orElse(null)))
                    .setRelationFields(ListUtils.toList(constraintAnnotation.relationFields()))
            ;
            dataMap.putIfAbsent(key, relation);
        }
        return dataMap;
    }

    @Override
    public String group() {
        return ModelField.MODEL_MODEL;
    }

    @Override
    public Class<?> metaModelClazz() {
        return ModelField.class;
    }

    @Override
    public ConverterType type() {
        return ConverterType.map;
    }

    @Override
    public List<String> signs(MetaNames names, Class source) {
        List<String> signs = new ArrayList<>();
        Model.Constraints constraintsAnnotation = AnnotationUtils.getAnnotation(source, Model.Constraints.class);
        for (Model.Constraint constraintAnnotation : Objects.requireNonNull(constraintsAnnotation).value()) {
            String model = names.getModel();
            String sign = ModelField.sign(names.getModel(), constraintAnnotation.foreignKey());
            signs.add(sign);
        }
        return signs;
    }

}
