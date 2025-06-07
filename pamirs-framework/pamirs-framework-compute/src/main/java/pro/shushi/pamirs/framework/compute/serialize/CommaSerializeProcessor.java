package pro.shushi.pamirs.framework.compute.serialize;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.orm.serialize.Serializer;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.enmu.SerializeEnum;
import pro.shushi.pamirs.meta.util.ExpressionUtils;
import pro.shushi.pamirs.meta.util.JsonUtils;
import pro.shushi.pamirs.meta.util.TypeUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 逗号表达式序列生成处理器实现
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/4 2:48 上午
 */
@SuppressWarnings("unused")
@Slf4j
@Component
public class CommaSerializeProcessor<T> implements Serializer<T, String> {

    @Override
    public String serialize(String ltype, T value) {
        if (null == value) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        List<?> list;
        if (value.getClass().isArray()) {
            list = Lists.newArrayList(value);
        } else if (List.class.isAssignableFrom(value.getClass())) {
            list = (List<?>) value;
        } else {
            return JsonUtils.toJSONString(value);
        }
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        for (Object obj : list) {
            String stringify = String.valueOf(obj).trim();
            if (stringify.contains(CharacterConstants.SEPARATOR_COMMA) || StringUtils.isBlank(stringify)) {
                sb.append("\"").append(stringify).append("\"");
            } else {
                sb.append(stringify);
            }
            sb.append(CharacterConstants.SEPARATOR_COMMA);
        }
        String exp = sb.toString();
        return exp.substring(0, exp.length() - 1);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T deserialize(String ltype, String ltypeT, String value, String format) {
        if (null == value) {
            return null;
        }
        List<Object> result = new ArrayList<>();
        Map<String, String> stringMap = new HashMap<>();
        String commaString = ExpressionUtils.ignoreString(value, stringMap);
        String[] commaStrings = commaString.split(CharacterConstants.SEPARATOR_COMMA);
        for (String comma : commaStrings) {
            comma = comma.trim();
            String placeholder = stringMap.get(comma);
            if (null != placeholder) {
                result.add(placeholder.replaceAll("\"", CharacterConstants.SEPARATOR_EMPTY));
            } else {
                Object item = TypeUtils.valueOfPrimary(ltypeT, comma, format);
                if (item == null) {
                    item = comma;
                }
                result.add(item);
            }
        }
        return (T) result;
    }

    @Override
    public String type() {
        return SerializeEnum.COMMA.value();
    }

}
