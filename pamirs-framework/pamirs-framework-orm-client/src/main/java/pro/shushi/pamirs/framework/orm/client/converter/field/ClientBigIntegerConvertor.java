package pro.shushi.pamirs.framework.orm.client.converter.field;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * BigInteger处理
 * <p>
 * 非递归遍历
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/18 6:35 下午
 */
@Component
public class ClientBigIntegerConvertor {

    public Object in(ModelFieldConfig fieldConfig, Object fieldValue) {
        if (BigInteger.class.getName().equals(fieldConfig.getLtype())
                && fieldValue instanceof BigDecimal) {
            return ((BigDecimal) fieldValue).toBigInteger();
        }
        return fieldValue;
    }

    @SuppressWarnings("unused")
    public Object out(ModelFieldConfig fieldConfig, Object fieldValue) {
        if (fieldValue instanceof BigInteger) {
            return new BigDecimal((BigInteger) fieldValue);
        }
        return fieldValue;
    }

}
