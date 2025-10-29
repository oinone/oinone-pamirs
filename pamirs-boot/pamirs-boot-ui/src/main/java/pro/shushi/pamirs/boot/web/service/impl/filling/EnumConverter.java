package pro.shushi.pamirs.boot.web.service.impl.filling;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.boot.base.tmodel.QuickFillingFailureDetail;
import pro.shushi.pamirs.boot.base.tmodel.QuickFillingField;
import pro.shushi.pamirs.boot.web.service.QuickFillingValueConverter;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.domain.model.DataDictionary;
import pro.shushi.pamirs.meta.domain.model.DataDictionaryItem;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

/**
 * @author Gesi at 9:35 on 2025/9/11
 */
@Slf4j
public class EnumConverter extends AbstractValueConverter implements QuickFillingValueConverter {

    public static final QuickFillingValueConverter INSTANCE = new EnumConverter();

    @Override
    public Object transform(QuickFillingField quickFillingField, String value, QuickFillingFailureDetail failureDetail) {
        ModelFieldConfig modelFieldConfig = quickFillingField.getModelConfigField();
        String dictionary = modelFieldConfig.getDictionary();
        DataDictionary dataDictionary = PamirsSession.getContext().getDictionary(dictionary);
        if (dataDictionary == null) {
            log.error("quick filling enumeration value convert error. cause: dictionary is not found. {}", dictionary);
            failureDetail.fail();
            return null;
        }
        TtypeEnum valueType = dataDictionary.getValueType();
        for (DataDictionaryItem option : dataDictionary.getOptions()) {
            if (StringUtils.equals(value, option.getDisplayName())) {
                if (dataDictionary.getBit() || TtypeEnum.INTEGER.equals(valueType)) {
                    return Long.parseLong(option.getValue());
                } else if (TtypeEnum.STRING.equals(valueType)) {
                    return option.getValue();
                }
            }
        }
        failureDetail.fail();
        return null;
    }
}
