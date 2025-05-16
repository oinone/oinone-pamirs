package pro.shushi.pamirs.framework.configure.annotation.core.converter.model;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelConverter;
import pro.shushi.pamirs.meta.api.core.configure.annotation.PrimaryFieldConverter;
import pro.shushi.pamirs.meta.api.dto.common.Message;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.ExecuteContext;
import pro.shushi.pamirs.meta.api.dto.meta.MetaNames;
import pro.shushi.pamirs.meta.common.enmu.IEnum;
import pro.shushi.pamirs.meta.domain.model.DataDictionaryItem;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.enmu.InformationLevelEnum;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.util.TypeUtils;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

import static pro.shushi.pamirs.framework.configure.annotation.emnu.AnnotationExpEnumerate.*;

/**
 * 模型字段枚举类型注解转化器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
@SuppressWarnings({"rawtypes", "unused"})
@Slf4j
@Order(102)
@Component
public class ModelFieldEnumConverter implements ModelConverter<ModelField, Field>, PrimaryFieldConverter {

    @Override
    public int priority() {
        return 102;
    }

    @Override
    @SuppressWarnings({"rawtypes", "unused"})
    public Result validate(ExecuteContext context, MetaNames names, Field field) {
        pro.shushi.pamirs.meta.annotation.Field.Enum typeAnnotation = AnnotationUtils.getAnnotation(field, pro.shushi.pamirs.meta.annotation.Field.Enum.class);
        Result result = new Result();
        Class<?> enumClazz = field.getType();
        if (TypeUtils.isCollection(enumClazz) || enumClazz.isArray()) {
            enumClazz = (Class<?>) TypeUtils.rawType(TypeUtils.getActualType(field));
        }
        if (null == typeAnnotation && !TypeUtils.isIEnumClass(enumClazz)) {
            return result.error();
        }
        String dictionary = Optional.ofNullable(typeAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.Enum::dictionary).orElse(null);
        if (!IEnum.class.isAssignableFrom(enumClazz) && StringUtils.isBlank(dictionary)) {
            result.addMessage(new Message().setLevel(InformationLevelEnum.ERROR)
                    .error(BASE_FIELD_ENUM_NO_DICTIONARY_ERROR)
                    .append(MessageFormat
                            .format("请配置@Field.enum的dictionary属性，因为字段没有使用IEnum枚举类声明，需要显式指定数据字典，class:{0}，field:{1}",
                                    field.getDeclaringClass().getName(), field.getName())));
            result.error();
            context.error();
        }
        String dictionaryFromEnum = Optional.ofNullable(AnnotationUtils.getAnnotation(enumClazz, Dict.class))
                .map(Dict::dictionary).filter(StringUtils::isNotBlank).orElse(
                        Optional.ofNullable(AnnotationUtils.getAnnotation(enumClazz, Dict.dictionary.class)).map(Dict.dictionary::value)
                                .filter(StringUtils::isNotBlank)
                                .orElse(enumClazz.getName())
                );
        if (IEnum.class.isAssignableFrom(enumClazz) && StringUtils.isNotBlank(dictionary) && !dictionaryFromEnum.equals(dictionary)) {
            result.addMessage(new Message().setLevel(InformationLevelEnum.ERROR)
                    .error(BASE_FIELD_ENUM_WRONG_DICTIONARY_ERROR)
                    .append(MessageFormat
                            .format("请配置@Field.enum的dictionary属性，且dictionary属性必须与字段枚举类型指定数据字典一致，class:{0}，field:{1}, dictionary:{2}",
                                    field.getDeclaringClass().getName(), field.getName(), dictionary)));
            result.error();
            context.error();
        }
        String fieldType = TypeUtils.getActualType(field).getTypeName();
        if (!(TypeUtils.isIEnumClass(fieldType) || TypeUtils.isBaseType(fieldType))) {
            result.addMessage(new Message().setLevel(InformationLevelEnum.ERROR)
                    .error(BASE_FIELD_UN_SUPPORT_ENUM_TYPE_ERROR)
                    .append(MessageFormat
                            .format("，类{0} 字段{1}的类型{2}",
                                    field.getDeclaringClass().getName(), field.getName(), field.getType().getName())));
            result.error();
            context.error();
        }
        return result;
    }

    @Override
    public ModelField convert(MetaNames names, Field field, ModelField modelField) {
        pro.shushi.pamirs.meta.annotation.Field.Enum fieldAnnotation = AnnotationUtils.getAnnotation(field, pro.shushi.pamirs.meta.annotation.Field.Enum.class);
        String dictionary = Models.enums().fetchDictionaryFromField(field);
        Class<?> type = TypeUtils.getClass(TypeUtils.getActualType(field).getTypeName());
        List<DataDictionaryItem> options = null;
        if (!TypeUtils.isBaseType(type)) {
            options = Models.enums().fetchEnumValues(type);
        }
        modelField.setDictionary(dictionary)
                .setSize(Optional.ofNullable(fieldAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.Enum::size).orElse(null))
                .setLimit(Optional.ofNullable(fieldAnnotation).map(pro.shushi.pamirs.meta.annotation.Field.Enum::limit).orElse(null))
                .setOptions(options)
                .setTtype(TtypeEnum.ENUM)
        ;
        return modelField;
    }

    @Override
    public String group() {
        return ModelField.MODEL_MODEL;
    }

    @Override
    public Class<?> metaModelClazz() {
        return ModelField.class;
    }

}
