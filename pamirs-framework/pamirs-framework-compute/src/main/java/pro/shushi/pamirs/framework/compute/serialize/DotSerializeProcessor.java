package pro.shushi.pamirs.framework.compute.serialize;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.orm.serialize.Serializer;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.enmu.SerializeEnum;
import pro.shushi.pamirs.meta.util.TypeUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 点表达式序列生成处理器实现
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/4 2:48 上午
 */
@SuppressWarnings("rawtypes")
@Slf4j
@Component
public class DotSerializeProcessor implements Serializer<Object, String> {

    @Override
    public String serialize(String ltype, Object value) {
        if (null == value) {
            return null;
        }
        if (List.class.isAssignableFrom(value.getClass())) {
            return StringUtils.join((List) value, CharacterConstants.SEPARATOR_DOT);
        } else {
            return StringUtils.join(Collections.singletonList(value), CharacterConstants.SEPARATOR_DOT);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object deserialize(String ltype, String ltypeT, String value, String format) {
        if (null == value) {
            return null;
        }
        String[] dots = value.split(CharacterConstants.SEPARATOR_ESCAPE_DOT);
        List list = new ArrayList();
        for (String dot : dots) {
            Object object = TypeUtils.valueOfPrimary(ltypeT, dot, null);
            list.add(object);
        }
        return list;
    }

    @Override
    public String type() {
        return SerializeEnum.DOT.value();
    }
}
