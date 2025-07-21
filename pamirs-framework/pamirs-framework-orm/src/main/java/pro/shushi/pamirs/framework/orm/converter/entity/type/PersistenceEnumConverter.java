package pro.shushi.pamirs.framework.orm.converter.entity.type;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.orm.converter.multi.MultiValueStrategy;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.enmu.Enums;
import pro.shushi.pamirs.meta.common.enmu.IEnum;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.util.BitUtil;
import pro.shushi.pamirs.meta.domain.model.DataDictionaryItem;
import pro.shushi.pamirs.meta.enmu.SerializeEnum;
import pro.shushi.pamirs.meta.util.TypeUtils;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static pro.shushi.pamirs.framework.orm.enmu.OrmExpEnumerate.BASE_ENUM_IS_NOT_EXISTS2_ERROR;
import static pro.shushi.pamirs.framework.orm.enmu.OrmExpEnumerate.BASE_ENUM_ITEM_IS_NOT_EXISTS2_ERROR;
import static pro.shushi.pamirs.meta.enmu.MetaExpEnumerate.BASE_CLASS_IS_NOT_EXISTS_ERROR;

/**
 * 持久层枚举转换服务
 * <p>
 * 递归遍历
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/18 6:35 下午
 */
@Component
public class PersistenceEnumConverter {

    /**
     * @deprecated please using {@link PersistenceEnumConverter#in(ModelConfig, ModelFieldConfig, Map)}
     */
    @Deprecated
    public void in(ModelFieldConfig fieldConfig, Map<String, Object> origin) {
        in(PamirsSession.getContext().getSimpleModelConfig(fieldConfig.getModel()), fieldConfig, origin);
    }

    @SuppressWarnings({"rawtypes"})
    public void in(ModelConfig modelConfig, ModelFieldConfig fieldConfig, Map<String, Object> origin) {
        if (modelConfig.isStaticConfig()) {
            return;
        }
        Object fieldValue = origin.get(fieldConfig.getLname());
        if (Long.class.getName().equals(fieldConfig.getLtype()) && fieldValue instanceof Long) {
            return;
        }
        boolean isMultiBit = null != fieldConfig.getMulti() && fieldConfig.getMulti()
                && SerializeEnum.BIT.value().equals(fieldConfig.getStoreSerialize());
        Object computeValue = MultiValueStrategy.submit(fieldConfig, fieldValue,
                (clazz, value) -> {
                    if (value instanceof IEnum) {
                        return ((IEnum) value).value();
                    }
                    return value;
                },
                v -> v,
                v -> v,
                params -> bitOr(fieldConfig, isMultiBit, params),
                params -> bitOr(fieldConfig, isMultiBit, params)
        );
        if (origin.containsKey(fieldConfig.getLname())) {
            origin.put(fieldConfig.getLname(), computeValue);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void out(ModelConfig modelConfig, ModelFieldConfig fieldConfig, Map<String, Object> origin) {
        if (modelConfig.isStaticConfig()) {
            return;
        }
        Object fieldValue = origin.get(fieldConfig.getLname());
        if (Long.class.getName().equals(fieldConfig.getLtype()) && fieldValue instanceof Long) {
            return;
        }
        boolean isMultiBit = null != fieldConfig.getMulti() && fieldConfig.getMulti()
                && SerializeEnum.BIT.value().equals(fieldConfig.getStoreSerialize());
        Object computeValue = MultiValueStrategy.submit(fieldConfig, fieldValue, (clazz, value) -> {
            if (null != value) {
                Class<?> valueClass = value.getClass();
                if (TypeUtils.isIEnumClass(valueClass)) {
                    return value;
                }
            }
            Class<?> enumerationClass;
            try {
                enumerationClass = TypeUtils.getClass(clazz);
            } catch (PamirsException e) {
                if (BASE_CLASS_IS_NOT_EXISTS_ERROR.code() == e.getCode()) {
                    enumerationClass = null;
                } else {
                    throw PamirsException.construct(BASE_ENUM_IS_NOT_EXISTS2_ERROR)
                            .appendMsg("name:" + fieldConfig.getLname()).errThrow();
                }
            }
            Object iEnum = null;
            if (null != enumerationClass) {
                if (TypeUtils.isIEnumClass(enumerationClass)) {
                    iEnum = Enums.getEnumByValue((Class<IEnum>) enumerationClass, (Serializable) value);
                    // FIXME: zbh 20250714 oracle 11g Boolean value is BigDecimal
                    if (iEnum == null && value instanceof Number) {
                        iEnum = Enums.getEnumByValue((Class<IEnum>) enumerationClass, String.valueOf(!"0".equals(String.valueOf(value))));
                    }
                } else {
                    iEnum = value;
                }
            } else {
                List<DataDictionaryItem> options = fieldConfig.getModelField().getOptions();
                for (DataDictionaryItem item : options) {
                    if (StringUtils.equals(item.getValue(), String.valueOf(fieldValue))) {
                        iEnum = item.getName();
                        break;
                    }
                }
            }
            if (null == iEnum && null != value) {
                throw PamirsException.construct(BASE_ENUM_ITEM_IS_NOT_EXISTS2_ERROR)
                        .appendMsg(MessageFormat.format("model:{0}，field:{1}, value:{2}", fieldConfig.getModel(), fieldConfig.getField(), value))
                        .errThrow();
            }
            return iEnum;
        }, param -> {
            if (isMultiBit) {
                return parseMultiBit(param);
            }
            return param;
        }, v -> v, v -> v, v -> v);
        if (origin.containsKey(fieldConfig.getLname())) {
            origin.put(fieldConfig.getLname(), computeValue);
        }
    }

    private List<Long> parseMultiBit(Object param) {
        long bits = Long.parseLong(String.valueOf(param));
        List<Long> value = new ArrayList<>();
        int position = 0;
        while (bits != 0) {
            if ((bits & 1) != 0) {
                if (position == 0) {
                    value.add(1L);
                } else {
                    value.add((long) (2 << (position - 1)));
                }
            }
            position++;
            bits = bits >>> 1;
        }
        return value;
    }

    private Object bitOr(ModelFieldConfig fieldConfig, boolean isMultiBit, List<?> params) {
        if (!isMultiBit) {
            return params;
        }
        //存储的二进制枚举字段求和计算
        if (fieldConfig.getStore()) {
            return BitUtil.bitOr(params);
        }
        //非存储的二进制枚举直接返回结果
        return params;
    }

    private Object bitOr(ModelFieldConfig fieldConfig, boolean isMultiBit, Object[] params) {
        if (!isMultiBit) {
            return params;
        }
        //存储的二进制枚举字段求和计算
        if (fieldConfig.getStore()) {
            return BitUtil.bitOr(params);
        }
        //非存储的二进制枚举直接返回结果
        return params;
    }

    private boolean isStaticConfig(ModelFieldConfig fieldConfig) {
        return PamirsSession.getContext().getSimpleModelConfig(fieldConfig.getModel()).isStaticConfig();
    }

}
