package pro.shushi.pamirs.framework.orm.json.handler;

import pro.shushi.pamirs.framework.orm.json.enmu.DataFeature;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.util.FieldFix;
import pro.shushi.pamirs.meta.util.DateUtils;
import pro.shushi.pamirs.meta.util.TypeUtils;

import java.util.Map;

/**
 * 时间处理
 * <p>
 * 2021/9/24 2:59 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class DataDateHandler {

    public static void toDate(ModelFieldConfig fieldConfig, Map<String, Object> origin, int features) {
        Object value = origin.get(fieldConfig.getLname());
        if (null == value) {
            return;
        }
        String format = FieldFix.fixFormat(fieldConfig.getModelField());
        boolean writeDateUsingToTimestamp = DataFeature.isEnabled(features, DataFeature.WriteDateUsingToTimestamp);
        Object dateValue;
        if (writeDateUsingToTimestamp) {
            dateValue = DateUtils.castDate(((Number) value).longValue(), fieldConfig.getLtype(), format);
        } else {
            dateValue = TypeUtils.valueOfPrimary(fieldConfig.getLtype(), String.valueOf(value), format);
        }
        if (null != dateValue) {
            origin.put(fieldConfig.getLname(), dateValue);
        }
    }

    public static void stringify(ModelFieldConfig fieldConfig, Map<String, Object> origin, int features) {
        Object value = origin.get(fieldConfig.getLname());
        if (null == value) {
            return;
        }
        boolean writeDateUsingToTimestamp = DataFeature.isEnabled(features, DataFeature.WriteDateUsingToTimestamp);
        Object dateSerializable;
        if (writeDateUsingToTimestamp) {
            dateSerializable = DateUtils.getTime(value);
        } else {
            String format = FieldFix.fixFormat(fieldConfig.getModelField());
            dateSerializable = DateUtils.convertDate(value, format);
        }
        if (null != dateSerializable) {
            origin.put(fieldConfig.getLname(), dateSerializable);
        }
    }

}
