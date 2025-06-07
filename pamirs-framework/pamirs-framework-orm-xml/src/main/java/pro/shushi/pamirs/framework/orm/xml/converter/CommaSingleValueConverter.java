package pro.shushi.pamirs.framework.orm.xml.converter;

import com.thoughtworks.xstream.converters.SingleValueConverter;
import org.apache.dubbo.common.utils.CollectionUtils;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * XML逗号表达式转换器
 * 2022/3/16 4:57 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class CommaSingleValueConverter implements SingleValueConverter {

    public Object fromString(String str) {
        if (null == str) {
            return null;
        }
        String[] parts = str.split(CharacterConstants.SEPARATOR_COMMA);
        if (0 == parts.length) {
            return null;
        }
        return Arrays.stream(parts).map(String::trim).collect(Collectors.toList());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public String toString(Object list) {
        if (null == list) {
            return null;
        }
        return CollectionUtils.join((List) list, CharacterConstants.SEPARATOR_COMMA);
    }

    public boolean canConvert(Class type) {
        return false;
    }

}