package pro.shushi.pamirs.framework.orm.converter.entity.type;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.orm.enmu.OrmExpEnumerate;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import sun.misc.BASE64Decoder;

import java.io.IOException;
import java.util.Map;

/**
 * 持久层Byte[]转换服务
 * <p>
 * 递归遍历
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/18 6:35 下午
 */
@Component
public class PersistenceByteConverter {

    public void in(ModelFieldConfig fieldConfig, Map<String, Object> origin) {
        Object value = origin.get(fieldConfig.getLname());
        if (null == value) {
            return;
        }
        if (null == fieldConfig.getMulti() || !fieldConfig.getMulti()) {
            return;
        }
        origin.put(fieldConfig.getLname(), ArrayUtils.toPrimitive((Byte[]) value));
    }

    public void out(ModelFieldConfig fieldConfig, Map<String, Object> origin) {
        Object value = origin.get(fieldConfig.getLname());
        if (null == value) {
            return;
        }
        if (null == fieldConfig.getMulti() || !fieldConfig.getMulti()) {
            return;
        }
        String temp = new String((byte[]) value);
        try {
            origin.put(fieldConfig.getLname(), new BASE64Decoder().decodeBuffer(temp.substring(1, temp.length() - 1)));
        } catch (IOException e) {
            throw PamirsException.construct(OrmExpEnumerate.BASE_BASE64_DECODE_ERROR, e).errThrow();
        }
    }

}
