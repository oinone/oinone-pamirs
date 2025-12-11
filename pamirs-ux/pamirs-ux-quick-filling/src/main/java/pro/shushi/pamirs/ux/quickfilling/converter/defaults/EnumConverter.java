package pro.shushi.pamirs.ux.quickfilling.converter.defaults;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.domain.model.DataDictionary;
import pro.shushi.pamirs.meta.domain.model.DataDictionaryItem;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.ux.quickfilling.converter.AbstractBasicQuickFillingConverter;
import pro.shushi.pamirs.ux.quickfilling.converter.QuickFillingColumn;
import pro.shushi.pamirs.ux.quickfilling.converter.QuickFillingConverter;
import pro.shushi.pamirs.ux.quickfilling.converter.QuickFillingRow;
import pro.shushi.pamirs.ux.quickfilling.enumeration.QuickFillingExpEnumerate;

import java.util.ArrayList;
import java.util.List;

/**
 * 枚举类型转换
 *
 * @author Adamancy Zhang at 21:14 on 2025-11-27
 */
public class EnumConverter extends AbstractBasicQuickFillingConverter implements QuickFillingConverter {

    private final boolean isBitValue;

    private final boolean isNumberValue;

    private final boolean isMulti;

    private final List<DataDictionaryItem> options;

    public EnumConverter(QuickFillingColumn column) {
        super(column);
        DataDictionary dataDictionary = PamirsSession.getContext().getDictionary(column.getDictionary());
        this.options = dataDictionary.getOptions();
        this.isBitValue = Boolean.TRUE.equals(dataDictionary.getBit());
        this.isNumberValue = this.isBitValue || TtypeEnum.INTEGER.equals(dataDictionary.getValueType());
        this.isMulti = column.isMulti();
    }

    @Override
    public void convert(QuickFillingRow row, String value) {
        if (isBitValue && isMulti) {
            convertBitMultiValue(row, value);
        } else {
            super.convert(row, value);
        }
    }

    @Override
    protected Object singleValueConvert(QuickFillingRow row, String value) {
        DataDictionaryItem option = findOption(value);
        if (option != null) {
            return option.getValue();
        }
        validateError(row, QuickFillingExpEnumerate.NON_ENUM_ERROR.msg());
        return null;
    }

    private DataDictionaryItem findOption(String name) {
        for (DataDictionaryItem option : options) {
            if (StringUtils.equals(name, option.getDisplayName()) || StringUtils.equals(name, option.getName())) {
                return option;
            }
        }
        return null;
    }

    private void convertBitMultiValue(QuickFillingRow row, String value) {
        if (isSkip(row, value)) {
            return;
        }
        QuickFillingColumn column = getColumn();
        List<String> results = new ArrayList<>();
        String[] valueList = value.split(CharacterConstants.SEPARATOR_COMMA);
        for (String valueItem : valueList) {
            if (StringUtils.isBlank(valueItem)) {
                continue;
            }
            valueItem = valueItem.trim();
            DataDictionaryItem option = findOption(valueItem);
            if (option == null) {
                validateError(row, QuickFillingExpEnumerate.NON_ENUM_ERROR.msg());
                return;
            }
            results.add(option.getValue());
        }
        if (results.isEmpty()) {
            if (column.isRequired()) {
                row.validateRequired(column.getField());
                return;
            }
        }
        setValue(row, results);
    }
}
