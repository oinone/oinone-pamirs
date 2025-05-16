package pro.shushi.pamirs.framework.orm.converter.entity.handler;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.orm.converter.multi.MultiValueStrategy;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.enmu.BaseEnum;
import pro.shushi.pamirs.meta.common.enmu.Enums;
import pro.shushi.pamirs.meta.common.enmu.IEnum;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.domain.model.DataDictionary;
import pro.shushi.pamirs.meta.domain.model.DataDictionaryItem;
import pro.shushi.pamirs.meta.util.TypeUtils;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static pro.shushi.pamirs.framework.orm.enmu.OrmExpEnumerate.BASE_ENUM_IS_NOT_EXISTS_ERROR;
import static pro.shushi.pamirs.framework.orm.enmu.OrmExpEnumerate.BASE_ENUM_ITEM_IS_NOT_EXISTS_ERROR;

/**
 * 字段枚举转换处理
 * <p>
 * 非递归遍历
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/18 6:35 下午
 */
@Component
public class EnumNamedHandler {

    @SuppressWarnings({"unchecked", "rawtypes"})
    public Object toEnum(ModelFieldConfig fieldConfig, Object fieldValue, @SuppressWarnings("unused") int features) {
        return dealEnum(fieldConfig, fieldValue, e -> e, (enumerationClass, item, value) -> {
            if (!item.getName().equals(value)) {
                return null;
            }
            String iEnumValueString = item.getValue();
            String ltype = fieldConfig.getLtype();
            if (null != fieldConfig.getMulti() && fieldConfig.getMulti()) {
                ltype = null == fieldConfig.getLtypeT() ? fieldConfig.getLtype() : fieldConfig.getLtypeT();
            }
            Object iEnumValue = TypeUtils.valueOfPrimary(ltype, iEnumValueString, null);
            if (BaseEnum.class.isAssignableFrom(enumerationClass)) {
                return BaseEnum.enumerate((Class<BaseEnum>) enumerationClass, fieldConfig.getDictionary(),
                        item.getName(), (Serializable) iEnumValue, item.getDisplayName(), item.getHelp(), item.getAttributes());
            } else {
                return iEnumValue;
            }
        }, e -> e, new JavaEnumFunction() {
            @Override
            public <E extends IEnum<?>> Object enumerate(Class<E> enumerationClass, Object value) {
                return Enums.getEnum(enumerationClass, (String) value);
            }
        },false);
    }

    @SuppressWarnings({"rawtypes"})
    public Object stringify(ModelFieldConfig fieldConfig, Object fieldValue, @SuppressWarnings("unused") int features) {
        return dealEnum(fieldConfig, fieldValue, IEnum::name, (enumerationClass, item, value) -> {
            if (null == item.getValue() && null == value
                    || null != item.getValue() && item.getValue().equals(String.valueOf(value))) {
                return item.getName();
            }
            return null;
        }, Enum::name, new JavaEnumFunction() {
            @SuppressWarnings({"unchecked", "rawtypes"})
            @Override
            public <E extends IEnum<?>> Object enumerate(Class<E> enumerationClass, Object fieldValue) {
                return Enums.getNameByValue((Class) enumerationClass, (Serializable) fieldValue);
            }
        }, true);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Object dealEnum(ModelFieldConfig fieldConfig, Object fieldValue,
                            Function<IEnum, Object> iEnumFunction, BaseEnumFunction baseEnumFunction,
                            Function<Enum, Object> javaEnumFunction, JavaEnumFunction javaValueEnumFunction,
                            boolean isOut) {
        return MultiValueStrategy.submit(fieldConfig, fieldValue, (clazz, value) -> {
            Class<?> enumerationClass = fetchEnumClass(fieldConfig, clazz, value);
            Object iEnum = null;
            Class<?> fieldValueClass = null != value ? value.getClass() : null;
            if (null == enumerationClass) {
                List<DataDictionaryItem> options = fieldConfig.getModelField().getOptions();
                for (DataDictionaryItem item : options) {
                    if (isOut) {
                        if (StringUtils.equals(item.getValue(), String.valueOf(value))) {
                            iEnum = item.getName();
                            break;
                        }
                    } else {
                        if (StringUtils.equals(item.getName(), String.valueOf(value))) {
                            iEnum = item.getValue();
                            break;
                        }
                    }
                }
            } else {
                if (!enumerationClass.isEnum()) {
                    if (null != fieldValueClass && IEnum.class.isAssignableFrom(fieldValueClass)
                            && !BaseEnum.class.getName().equals(fieldValueClass.getName())) {
                        iEnum = iEnumFunction.apply((IEnum) value);
                    } else {
                        List<DataDictionaryItem> options = fieldConfig.getModelField().getOptions();
                        if (CollectionUtils.isNotEmpty(options)) {
                            if (value instanceof List) {
                                List<Object> list = new ArrayList<>();
                                for (Object object : (List) value) {
                                    Object m;
                                    for (DataDictionaryItem item : options) {
                                        m = baseEnumFunction.enumerate(enumerationClass, item, object);
                                        if (null != m) {
                                            list.add(m);
                                            break;
                                        }
                                    }
                                }
                                iEnum = list;
                            } else {
                                for (DataDictionaryItem item : options) {
                                    iEnum = baseEnumFunction.enumerate(enumerationClass, item, value);
                                    if (null != iEnum) {
                                        break;
                                    }
                                }
                            }
                        }
                    }
                } else {
                    if (null != fieldValueClass && IEnum.class.isAssignableFrom(fieldValueClass)) {
                        iEnum = iEnumFunction.apply((IEnum) value);
                    } else if (null != fieldValueClass && fieldValueClass.isEnum()) {
                        iEnum = javaEnumFunction.apply((Enum) value);
                    } else {
                        iEnum = javaValueEnumFunction.enumerate((Class<IEnum>) enumerationClass, value);
                    }
                }
            }
            if (null == iEnum && null != value) {
                throw PamirsException.construct(BASE_ENUM_ITEM_IS_NOT_EXISTS_ERROR)
                        .appendMsg(MessageFormat.format("model:{0}, name:{1}，value:{2}",
                                fieldConfig.getModel(), fieldConfig.getLname(), value)).errThrow();
            }
            return iEnum;
        });
    }

    private Class<?> fetchEnumClass(ModelFieldConfig fieldConfig, String clazz, Object value) {
        Class<?> enumerationClass = null;
        if (null != clazz) {
            try {
                enumerationClass = Class.forName(clazz);
            } catch (ClassNotFoundException e) {
                DataDictionary dict = PamirsSession.getContext().getDictionary(fieldConfig.getDictionary());
                if (null == dict) {
                    throw PamirsException.construct(BASE_ENUM_IS_NOT_EXISTS_ERROR)
                            .appendMsg(MessageFormat.format("name:{0}，value:{1}", fieldConfig.getLname(), value)).errThrow();
                }
                return enumerationClass;
            }
        } else {
            String         dictionary     = fieldConfig.getDictionary();
            DataDictionary dataDictionary = PamirsSession.getContext().getDictionary(dictionary);
            enumerationClass = TypeUtils.getClass(dataDictionary.getLname());
        }
        return enumerationClass;
    }

    interface BaseEnumFunction {
        Object enumerate(Class<?> enumerationClass, DataDictionaryItem item, Object fieldValue);
    }

    interface JavaEnumFunction {
        <E extends IEnum<?>> Object enumerate(Class<E> enumerationClass, Object fieldValue);
    }

}
