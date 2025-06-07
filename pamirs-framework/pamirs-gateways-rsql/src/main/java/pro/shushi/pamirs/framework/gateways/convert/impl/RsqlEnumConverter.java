package pro.shushi.pamirs.framework.gateways.convert.impl;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.gateways.convert.RsqlValueConverter;
import pro.shushi.pamirs.framework.gateways.util.BooleanHelper;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.domain.model.DataDictionary;
import pro.shushi.pamirs.meta.domain.model.DataDictionaryItem;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

import java.util.List;

import static pro.shushi.pamirs.framework.gateways.rsql.enmu.RsqlExpEnumerate.BASE_ARGUMENTS_ERROR;

/**
 * 枚举值转换
 *
 * @author Adamancy Zhang at 15:12 on 2021-09-03
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
@SPI.Service(RsqlEnumConverter.SPI_NAME)
public class RsqlEnumConverter extends AbstractRsqlValueConverter implements RsqlValueConverter {

    public static final String SPI_NAME = "enumeration";

    @Override
    public boolean match(ModelFieldConfig modelField) {
        return TtypeEnum.ENUM.value().equals(fetchRealTtype(modelField));
    }

    @Override
    public Object convert(ModelFieldConfig modelField, Object argument) {
        DataDictionary dictionary = PamirsSession.getContext().getDictionary(modelField.getDictionary());
        if (dictionary == null) {
            throw PamirsException.construct(BASE_ARGUMENTS_ERROR).errThrow();
        }
        List<DataDictionaryItem> options = dictionary.getOptions();
        for (DataDictionaryItem dataDictionaryItem : options) {
            if (argument.equals(dataDictionaryItem.getName())) {
                String ttype = dictionary.getValueType().value();
                if (TtypeEnum.isBoolType(ttype)) {
                    String value = dataDictionaryItem.getValue();
                    if (value == null) {
                        argument = null;
                    } else {
                        if (BooleanHelper.isTrue(dataDictionaryItem.getValue())) {
                            argument = Boolean.TRUE;
                        } else {
                            argument = Boolean.FALSE;
                        }
                    }
                } else if (TtypeEnum.INTEGER.value().equals(ttype) || dictionary.getBit()) {
                    argument = Long.valueOf(dataDictionaryItem.getValue());
                } else {
                    argument = dataDictionaryItem.getValue();
                }
                break;
            }
        }
        return argument;
    }
}
