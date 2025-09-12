package pro.shushi.pamirs.boot.web.service.impl.filling;

import org.springframework.stereotype.Service;
import pro.shushi.pamirs.boot.base.enmu.QuickFillingFailCodeEnum;
import pro.shushi.pamirs.boot.base.tmodel.QuickFillingFailureDetail;
import pro.shushi.pamirs.boot.base.tmodel.QuickFillingField;
import pro.shushi.pamirs.boot.web.service.QuickFillingValueConverter;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.common.enmu.BaseEnum;
import pro.shushi.pamirs.meta.common.enmu.IEnum;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

/**
 * @author Gesi at 9:35 on 2025/9/11
 */
@Service
public class EnumConverter extends AbstractValueConverter implements QuickFillingValueConverter {

    @Override
    public boolean canTransform(TtypeEnum ttype) {
        return TtypeEnum.ENUM.equals(ttype);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Object transform(QuickFillingField quickFillingField, String value, QuickFillingFailureDetail failureDetail) {
        ModelFieldConfig modelConfigField = quickFillingField.getModelConfigField();
        Class<?> valueClass;
        try {
            valueClass = Class.forName(modelConfigField.getLtype());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        if (BaseEnum.class.isAssignableFrom(valueClass)) {
            return BaseEnum.getEnum((Class<? extends BaseEnum<?, ?>>) valueClass, value);
        } else if (Enum.class.isAssignableFrom(valueClass)) {
            return Enum.valueOf((Class<? extends Enum>) valueClass, value);
        }

        failureDetail.fail(QuickFillingFailCodeEnum.TYPE_INCOMPATIBLE, value);
        return null;
    }
}
