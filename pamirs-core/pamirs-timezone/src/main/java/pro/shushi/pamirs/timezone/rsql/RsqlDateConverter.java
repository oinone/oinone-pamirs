package pro.shushi.pamirs.timezone.rsql;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.timezone.TimezoneConverter;
import pro.shushi.pamirs.framework.gateways.convert.RsqlValueConverter;
import pro.shushi.pamirs.framework.gateways.convert.impl.AbstractRsqlValueConverter;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.util.DateUtils;
import pro.shushi.pamirs.timezone.session.TimezoneSession;

import java.util.TimeZone;

/**
 * 日期值转换
 *
 * @author Adamancy Zhang at 15:34 on 2021-09-03
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
@SPI.Service(RsqlDateConverter.SPI_NAME)
public class RsqlDateConverter extends AbstractRsqlValueConverter implements RsqlValueConverter {

    public static final String SPI_NAME = "date";

    @Override
    public boolean match(ModelFieldConfig modelField) {
        return TtypeEnum.isDateType(fetchRealTtype(modelField));
    }

    @Override
    public Object convert(ModelFieldConfig modelField, Object argument) {
        String pattern = modelField.getFormat();
        argument = DateUtils.castDate(argument, modelField.getLtype(), pattern);
        TimeZone timezone = TimezoneSession.getTimezone();
        if (timezone != null) {
            argument = TimezoneConverter.newInstance(timezone, TimeZone.getDefault()).convert(argument);
        }
        return DateUtils.convertDate(argument, pattern);
    }
}
