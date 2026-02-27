package pro.shushi.pamirs.meta.api.core.orm.systems.enums;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.CommonApiFactory;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.compute.systems.enmu.EnumProcessor;
import pro.shushi.pamirs.meta.common.enmu.BaseEnum;
import pro.shushi.pamirs.meta.common.enmu.BitEnum;
import pro.shushi.pamirs.meta.common.enmu.Enums;
import pro.shushi.pamirs.meta.common.enmu.IEnum;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.domain.model.DataDictionary;
import pro.shushi.pamirs.meta.domain.model.DataDictionaryItem;
import pro.shushi.pamirs.meta.enmu.ActiveEnum;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.util.TypeUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static pro.shushi.pamirs.meta.enmu.MetaExpEnumerate.*;

/**
 * 枚举处理器默认实现
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/4 2:48 上午
 */
@SuppressWarnings({"rawtypes"})
@Slf4j
public class BaseEnumProcessor implements EnumProcessor<DataDictionary> {

    @Override
    public String fetchDictionaryFromClass(Class enumClass) {
        return Optional.ofNullable(AnnotationUtils.getAnnotation(enumClass, Dict.class))
                .map(Dict::dictionary).filter(StringUtils::isNotBlank).orElse(
                        Optional.ofNullable(AnnotationUtils.getAnnotation(enumClass, Dict.dictionary.class))
                                .map(Dict.dictionary::value).filter(StringUtils::isNotBlank)
                                .orElse(enumClass.getName()
                                )
                );
    }

    @Override
    public String fetchDictionaryFromField(Field field) {
        Class<?> enumClazz = field.getType();
        if (TypeUtils.isCollection(enumClazz) || enumClazz.isArray()) {
            enumClazz = (Class<?>) TypeUtils.getActualType(field);
        }
        String dictionary = Optional.ofNullable(AnnotationUtils.getAnnotation(field, pro.shushi.pamirs.meta.annotation.Field.Enum.class))
                .map(pro.shushi.pamirs.meta.annotation.Field.Enum::dictionary).orElse(null);
        if (StringUtils.isBlank(dictionary)) {
            if (!TypeUtils.isIEnumClass(enumClazz)) {
                throw PamirsException.construct(BASE_FIELD_ENUM_CONFIG_ERROR).appendMsg(enumClazz.getName()).errThrow();
            }
            String dictionaryFromEnum = fetchDictionaryFromClass(enumClazz);
            if (StringUtils.isNotBlank(dictionaryFromEnum)) {
                dictionary = dictionaryFromEnum;
            }
        }
        return dictionary;
    }

    @Override
    public DataDictionary fetchDataDictionaryFromEnum(String module, Class enumClass) {
        return fillDataDictionaryFromEnum(new DataDictionary(), module, enumClass);
    }

    @Override
    public DataDictionary fillDataDictionaryFromEnum(DataDictionary dataDictionary, String module, Class enumClass) {
        String enumObject = enumClass.getName();
        if (!TypeUtils.isIEnumClass(enumClass)) {
            throw PamirsException.construct(BASE_FIELD_ENUM_CONFIG_ERROR).appendMsg(enumClass.getName()).errThrow();
        }
        String dictionary = fetchDictionaryFromClass(enumClass);
        SystemSourceEnum source = Optional.ofNullable(AnnotationUtils.getAnnotation(enumClass, Base.class))
                .map(Base::value).orElse(null);
        Dict dict = AnnotationUtils.getAnnotation(enumClass, Dict.class);
        String displayName = Optional.ofNullable(dict).map(Dict::displayName).filter(StringUtils::isNotBlank).orElse(enumClass.getSimpleName());
        String name = Optional.ofNullable(dict).map(Dict::name).filter(StringUtils::isNotBlank).orElse(StringUtils.uncapitalize(enumClass.getSimpleName()));
        String summary = Optional.ofNullable(dict).map(Dict::summary).orElse(null);
        int type = Optional.ofNullable(dict).map(Dict::type).orElse(1);
        dataDictionary.setDisplayName(displayName)
                .setName(name)
                .setDictionary(dictionary)
                .setSummary(summary)
                .setOptions(fetchEnumValues(enumClass))
                .setModule(module)
                .setValueType(BaseEnum.getEnumByValue(TtypeEnum.class, fetchEnumValueTtype(enumClass)))
                .setLname(enumObject)
                .setType(type)
                .setBit(BitEnum.class.isAssignableFrom(enumClass))
                .setSystemSource(source)
        ;
        return dataDictionary;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<DataDictionaryItem> fetchEnumValues(Class enumClass) {
        try {
            if (!IEnum.class.isAssignableFrom(enumClass)) {
                throw PamirsException.construct(BASE_ENUM_TYPE_ERROR_ERROR).appendMsg("class:" + enumClass).errThrow();
            }
            SystemSourceEnum source = Optional.ofNullable(AnnotationUtils.getAnnotation(enumClass, Base.class))
                    .map(Base::value).orElse(null);
            boolean showHelp = Optional.ofNullable(AnnotationUtils.getAnnotation(enumClass, Dict.class))
                    .map(Dict::showHelp)
                    .orElse(false);
            List<DataDictionaryItem> enumValues = new ArrayList<>();
            List<IEnum> enums = Enums.getEnumList((Class<IEnum>) enumClass);
            for (IEnum one : enums) {
                // FIXME: zbh 20260227 此处需要补充使用 Prop 注解设置 DSL 属性，并通过 attributes 属性传递。
                String displayName = one.displayName();
                String name = one.name();
                DataDictionaryItem dataDictionaryItem = new DataDictionaryItem()
                        .setDisplayName(displayName)
                        .setName(name)
                        .setValue(TypeUtils.stringValueOf(one.value()))
                        .setState(ActiveEnum.ACTIVE)
                        .setSource(source);
                if (showHelp) {
                    String help = one.help();
                    if (StringUtils.isNotBlank(help)) {
                        dataDictionaryItem.setHelp(help);
                    }
                }
                enumValues.add(dataDictionaryItem);
            }
            return enumValues;
        } catch (Exception e) {
            throw PamirsException.construct(BASE_ENUM_CONFIG_ERROR, e).appendMsg("class:" + enumClass).errThrow();
        }
    }

    @Override
    public String fetchEnumValueTtype(Field enumField) {
        try {
            Class<?> enumClazz = enumField.getType();
            if (TypeUtils.isCollection(enumClazz)) {
                enumClazz = (Class<?>) TypeUtils.getActualType(enumField);
            }
            if (!TypeUtils.isIEnumClass(enumClazz)) {
                return CommonApiFactory.getTypeProcessor().defaultTtypeFromLtype(enumClazz.getName(), null, null);
            }
            for (Field enumInnerField : enumClazz.getDeclaredFields()) {
                if ("value".equals(enumInnerField.getName())) {
                    Class enumInnerFieldType = enumInnerField.getType();
                    return CommonApiFactory.getTypeProcessor().defaultTtypeFromLtype(enumInnerFieldType.getName(), null, null);
                }
            }
        } catch (Exception e) {
            throw PamirsException.construct(BASE_CONVERT_ENUM_ERROR, e).appendMsg("class: " + enumField.getDeclaringClass().getName() + ", field: " + enumField.getName()).errThrow();
        }
        return null;
    }

    @Override
    public String fetchEnumValueTtype(Class enumClazz) {
        try {
            if (!TypeUtils.isIEnumClass(enumClazz)) {
                throw PamirsException.construct(BASE_FIELD_NOT_ENUM_ERROR).appendMsg("不是枚举类型:" + enumClazz.getName()).errThrow();
            }
            String valueType;
            if (enumClazz.isEnum()) {
                valueType = Objects.requireNonNull(TypeUtils.getEnumInterfaceGenericTypes(enumClazz))[0].getTypeName();
            } else {
                valueType = Objects.requireNonNull(TypeUtils.getSuperClassGenericType(enumClazz)).getTypeName();
            }
            return Models.types().defaultTtypeFromLtype(valueType, null, null);
        } catch (Exception e) {
            throw PamirsException.construct(BASE_CONVERT_ENUM_ERROR, e).appendMsg("class:" + enumClazz).errThrow();
        }
    }

}
