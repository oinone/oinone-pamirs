package pro.shushi.pamirs.framework.connectors.data.elastic.rsql;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.gateways.convert.impl.AbstractRsqlValueConverter;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.enmu.DateFormatEnum;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.util.DateUtils;

/**
 * ES日期值转换
 *
 * @author Adamancy Zhang at 15:34 on 2021-09-03
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
public class EsRsqlDateConverter extends AbstractRsqlValueConverter {

    public boolean match(ModelFieldConfig modelField) {
        return TtypeEnum.isDateType(fetchRealTtype(modelField));
    }

    /**
     * es的存储结构为时间戳，做下转换
     *
     * @param modelField
     * @param argument
     * @return
     */
    public static Object convert(ModelFieldConfig modelField, Object argument) {
        if (argument instanceof String) {
            //先转成时间类型
            String pattern = modelField.getFormat();
            argument = DateUtils.castDate(argument, modelField.getLtype(), pattern);
        }
        return DateUtils.convertDate(argument, DateFormatEnum.TIMESTAMP.value());
    }

}
