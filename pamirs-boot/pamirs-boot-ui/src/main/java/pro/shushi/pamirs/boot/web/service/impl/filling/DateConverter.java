package pro.shushi.pamirs.boot.web.service.impl.filling;

import pro.shushi.pamirs.boot.base.tmodel.QuickFillingFailureDetail;
import pro.shushi.pamirs.boot.base.tmodel.QuickFillingField;
import pro.shushi.pamirs.boot.web.service.QuickFillingValueConverter;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.util.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Gesi at 9:35 on 2025/9/11
 */
@Slf4j
public class DateConverter extends AbstractValueConverter implements QuickFillingValueConverter {

    public static final QuickFillingValueConverter INSTANCE = new DateConverter();

    @Override
    public Object transform(QuickFillingField quickFillingField, String value, QuickFillingFailureDetail failureDetail) {
        Date date;
        try {
            date = getDate(value, quickFillingField);
        } catch (Exception e) {
            failureDetail.fail();
            log.error("quick filling date value convert error. field: {}, value: {}", quickFillingField.getField(), value, e);
            return null;
        }
        return date;
    }

    private Date getDate(String value, QuickFillingField quickFillingField) {
        ModelFieldConfig modelFieldConfig = quickFillingField.getModelConfigField();
        String ttype = modelFieldConfig.getTtype();
        if (TtypeEnum.RELATED.value().equals(ttype)) {
            ttype = modelFieldConfig.getRelatedTtype();
        }

        Date date = null;
        if (TtypeEnum.YEAR.value().equals(ttype)) {
            try {
                date = DateUtils.formatDate(value, "yyyy");
            } catch (Exception ignored) {
            }
        } else {
            String[] datePatterns = {
                    "yyyy/MM/dd",
                    "yyyy-MM-dd",
                    "yyyy.MM.dd"
            };
            String[] timePatterns = {
                    "HH:mm:ss"
            };

            if (TtypeEnum.DATE.value().equals(ttype)) {
                for (String datePattern : datePatterns) {
                    try {
                        date = new SimpleDateFormat(datePattern).parse(value);
                    } catch (ParseException ignored) {
                    }
                    if (date != null) {
                        return date;
                    }
                }
            } else if (TtypeEnum.TIME.value().equals(ttype)) {
                for (String timePattern : timePatterns) {
                    try {
                        date = new SimpleDateFormat(timePattern).parse(value);
                    } catch (ParseException ignored) {
                    }
                    if (date != null) {
                        return date;
                    }
                }
            } else {
                for (String datePattern : datePatterns) {
                    for (String timePattern : timePatterns) {
                        try {
                            date = new SimpleDateFormat(datePattern + " " + timePattern).parse(value);
                        } catch (ParseException ignored) {
                        }
                        if (date != null) {
                            return date;
                        }
                    }
                }
            }

        }

        if (date == null) {
            return DateUtils.formatDate(value, modelFieldConfig.getFormat());
        }
        return date;
    }
}
