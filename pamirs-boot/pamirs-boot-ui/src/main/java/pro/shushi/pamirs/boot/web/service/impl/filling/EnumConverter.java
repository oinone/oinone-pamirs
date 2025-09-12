package pro.shushi.pamirs.boot.web.service.impl.filling;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.boot.base.enmu.QuickFillingFailCodeEnum;
import pro.shushi.pamirs.boot.base.tmodel.QuickFillingFailureDetail;
import pro.shushi.pamirs.boot.base.tmodel.QuickFillingField;
import pro.shushi.pamirs.boot.web.service.QuickFillingValueConverter;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.common.enmu.BaseEnum;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author Gesi at 9:35 on 2025/9/11
 */
@Service
@Slf4j
public class EnumConverter extends AbstractValueConverter implements QuickFillingValueConverter {

    @Override
    public boolean canTransform(TtypeEnum ttype) {
        return TtypeEnum.ENUM.equals(ttype);
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public Object transform(QuickFillingField quickFillingField, String value, QuickFillingFailureDetail failureDetail) {
        ModelFieldConfig modelFieldConfig = quickFillingField.getModelConfigField();
        Class<?> valueClass;
        try {
            valueClass = Class.forName(Boolean.TRUE.equals(modelFieldConfig.getMulti()) ? modelFieldConfig.getLtypeT() : modelFieldConfig.getLtype());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        if (BaseEnum.class.isAssignableFrom(valueClass)) {
            List<? extends BaseEnum<?, ?>> enumList = BaseEnum.getEnumList((Class<? extends BaseEnum<?, ?>>) valueClass);
            for (BaseEnum<?, ?> baseEnum : enumList) {
                if (StringUtils.equals(baseEnum.displayName(), value)) {
                    return baseEnum;
                }
            }
        } else if (valueClass.isEnum()) {
            return resolveJavaEnum(valueClass, value, failureDetail);
        }

        failureDetail.fail(QuickFillingFailCodeEnum.TYPE_INCOMPATIBLE);
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <E extends Enum<E>> E resolveJavaEnum(Class<?> enumClass, String value, QuickFillingFailureDetail failureDetail) {
        Object[] constants = enumClass.getEnumConstants();

        try {
            Field displayNameField = null;
            try {
                displayNameField = enumClass.getDeclaredField("displayName");
                displayNameField.setAccessible(true);
            } catch (NoSuchFieldException ignored) {
            }

            for (Object constant : constants) {
                E e = (E) constant;
                if (displayNameField != null) {
                    String displayNameValue = displayNameField.get(e) + "";
                    if (StringUtils.equals(value, displayNameValue)) {
                        return e;
                    }
                }
                if (StringUtils.equals(value, e.name())) {
                    return e;
                }
            }
        } catch (IllegalAccessException e) {
            log.error("快速填报枚举转换失败", e);
            failureDetail.fail(QuickFillingFailCodeEnum.TYPE_INCOMPATIBLE);
        }

        failureDetail.fail(QuickFillingFailCodeEnum.TYPE_INCOMPATIBLE);
        return null;
    }
}
