package pro.shushi.pamirs.framework.gateways.convert.impl;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.gateways.convert.RsqlValueConverter;
import pro.shushi.pamirs.framework.gateways.util.BooleanHelper;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

/**
 * Bool值转换
 *
 * @author Adamancy Zhang at 15:12 on 2021-09-03
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
@SPI.Service(RsqlBooleanConverter.SPI_NAME)
public class RsqlBooleanConverter extends AbstractRsqlValueConverter implements RsqlValueConverter {

    public static final String SPI_NAME = "boolean";

    @Override
    public boolean match(ModelFieldConfig modelField) {
        return TtypeEnum.isBoolType(fetchRealTtype(modelField));
    }

    @Override
    public Object convert(ModelFieldConfig modelField, Object argument) {
        if (BooleanHelper.isTrue(argument)) {
            argument = Boolean.TRUE;
        } else {
            argument = Boolean.FALSE;
        }
        return argument;
    }
}
