package pro.shushi.pamirs.filling.converter;

import org.apache.commons.lang3.StringUtils;
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
    public Object singleValueConvert(QuickFillingContext context, String value) {
        ModelFieldConfig modelFieldConfig = context.getModelFieldConfig();
        String dictionary = modelFieldConfig.getDictionary();
        DataDictionary dataDictionary = PamirsSession.getContext().getDictionary(dictionary);
        if (dataDictionary == null) {
            log.error("quick filling enumeration value convert error. cause: dictionary is not found. {}", dictionary);
            context.fail();
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
        context.fail();
        return null;
    }
}
