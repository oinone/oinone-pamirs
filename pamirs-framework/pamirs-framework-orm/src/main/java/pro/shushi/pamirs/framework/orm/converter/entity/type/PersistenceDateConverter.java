package pro.shushi.pamirs.framework.orm.converter.entity.type;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.enmu.DateFormatEnum;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.util.DateUtils;
import pro.shushi.pamirs.meta.util.FieldFix;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * 持久层时间转换服务
 * <p>
 * 递归遍历
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/18 6:35 下午
 */
@Component
public class PersistenceDateConverter {

    @SuppressWarnings("unused")
    public void in(ModelFieldConfig fieldConfig, Map<String, Object> origin) {
        Object value = origin.get(fieldConfig.getLname());
        if (value == null) {
            return;
        }
        if (value instanceof Date) {
            if (TtypeEnum.YEAR.value().equals(fieldConfig.getTtype())) {
                // year类型,一次执行多条update时,数据库报错. 因此先处理成字符串. 其他时间类型最终发包时使用Timestamp对象,数据库都兼容
                origin.put(fieldConfig.getLname(), Integer.parseInt(DateUtils.formatDate((Date) value, DateFormatEnum.YEAR.value())));
            } else if (TtypeEnum.DATE.value().equals(fieldConfig.getTtype())) {
                // 日期(yyyy-MM-dd)类型，如果是时间保存，超过500毫秒会进一。（2023-10-31 23:59:59.999==>>2023-11-01,正确的应该是：2023-10-31）;
                //      因此需要格式化，不能使用数据库的默认方式
                origin.put(fieldConfig.getLname(), DateUtils.parseByDayPattern(DateUtils.formatDate((Date) value, DateFormatEnum.DATE.value())));
            }
        }
    }

    public void out(ModelFieldConfig fieldConfig, Map<String, Object> origin) {
        Object value = origin.get(fieldConfig.getLname());
        if (null == value) {
            return;
        }
        String ltype = fieldConfig.getLtype();
        if (TtypeEnum.YEAR.value().equals(fieldConfig.getTtype())) {
            if (!Integer.class.getName().equals(ltype) && !Long.class.getName().equals(ltype) && !BigDecimal.class.getName().equals(ltype)
                    && (value instanceof Integer || value instanceof Long || value instanceof BigDecimal)) {
                String format = FieldFix.fixFormat(fieldConfig.getModelField());
                if (DateFormatEnum.YEAR.value().equals(format)) {
                    value = DateUtils.parseByDayPattern(String.format("%s-01-01", value));
                    origin.put(fieldConfig.getLname(),
                            DateUtils.castDate(value, ltype, DateFormatEnum.YEAR.value()));
                    return;
                }
            }
        }
        origin.put(fieldConfig.getLname(),
                DateUtils.castDate(value, ltype, FieldFix.fixFormat(fieldConfig.getModelField())));
    }

}
