package pro.shushi.pamirs.framework.orm.client.converter.entity;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.common.entry.NullValue;
import pro.shushi.pamirs.framework.orm.client.converter.field.ClientBigIntegerConvertor;
import pro.shushi.pamirs.framework.orm.enmu.OrmExpEnumerate;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 大整数转换处理
 * <p>
 * 非递归遍历
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/18 6:35 下午
 */
@Component
@Slf4j
public class ClientBigIntegerConverter {

    @Resource
    private ClientBigIntegerConvertor fieldBigIntegerConvertor;

    public void in(ModelFieldConfig fieldConfig, Map<String, Object> origin) {
        Object value = origin.get(fieldConfig.getLname());
        if (null == value || NullValue.INSTANCE.equals(value)) {
            return;
        }
        try {
            value = fieldBigIntegerConvertor.in(fieldConfig, value);
            origin.put(fieldConfig.getLname(), value);
        } catch (Throwable e) {
            String errorMsg = String.format("ClientBigIntegerConverter in method error, model：[%s],field：[%s],tType:[%s],lType:[%s],value:[%s],valueClass:[%s]", fieldConfig.getModel(), fieldConfig.getField(), fieldConfig.getTtype(), fieldConfig.getLtype(), value, value.getClass());
            log.error("ClientBigIntegerConverter in method error", errorMsg);
            throw PamirsException.construct(OrmExpEnumerate.BASE_FIELD_CONVERTER_ERROR, e).appendMsg(errorMsg).errThrow();
        }
    }

    public void out(ModelFieldConfig fieldConfig, Map<String, Object> origin) {
        String lname = fieldConfig.getLname();
        Object value = origin.get(lname);
        if (null == value || NullValue.INSTANCE.equals(value)) {
            return;
        }
        try {
            value = fieldBigIntegerConvertor.out(fieldConfig, value);
            origin.put(lname, value);
        } catch (Throwable e) {
            String errorMsg = String.format("ClientBigIntegerConverter out method error, model：[%s],field：[%s],tType:[%s],lType:[%s],value:[%s],valueClass:[%s]", fieldConfig.getModel(), fieldConfig.getField(), fieldConfig.getTtype(), fieldConfig.getLtype(), value, value.getClass());
            log.error("ClientBigIntegerConverter out method error", errorMsg);
            throw PamirsException.construct(OrmExpEnumerate.BASE_FIELD_CONVERTER_ERROR, e).appendMsg(errorMsg).errThrow();
        }
    }

}
