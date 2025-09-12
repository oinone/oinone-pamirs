package pro.shushi.pamirs.boot.web.service.impl.filling;

import org.springframework.stereotype.Service;
import pro.shushi.pamirs.boot.base.enmu.QuickFillingFailCodeEnum;
import pro.shushi.pamirs.boot.base.tmodel.QuickFillingFailureDetail;
import pro.shushi.pamirs.boot.base.tmodel.QuickFillingField;
import pro.shushi.pamirs.boot.web.service.QuickFillingValueConverter;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.util.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Gesi at 9:35 on 2025/9/11
 */
@Service
public class DateConverter extends AbstractValueConverter implements QuickFillingValueConverter {

    @Override
    public boolean canTransform(TtypeEnum ttype) {
        return TtypeEnum.isDateType(ttype.value());
    }

    @Override
    public Object transform(QuickFillingField quickFillingField, String value, QuickFillingFailureDetail failureDetail) {
        ModelFieldConfig modelFieldConfig = quickFillingField.getModelConfigField();
        Date date;
        try {
            date = getDate(value, quickFillingField);
        } catch (Exception e) {
            failureDetail.fail(QuickFillingFailCodeEnum.TYPE_INCOMPATIBLE);
            return null;
        }

        String ltype = Boolean.TRUE.equals(modelFieldConfig.getMulti()) ? modelFieldConfig.getLtypeT() : modelFieldConfig.getLtype();
        if (java.sql.Date.class.getName().equals(ltype)) {
            return new java.sql.Date(date.getTime());
        } else if (java.sql.Timestamp.class.getName().equals(ltype)) {
            return new java.sql.Timestamp(date.getTime());
        } else if (java.sql.Time.class.getName().equals(ltype)) {
            return new java.sql.Time(date.getTime());
        }
        return date;
    }


    private Date getDate(String value, QuickFillingField quickFillingField) {
        ModelFieldConfig modelFieldConfig = quickFillingField.getModelConfigField();
        String ttype = Boolean.TRUE.equals(modelFieldConfig.getMulti()) ? modelFieldConfig.getLtypeT() : modelFieldConfig.getLtype();

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
