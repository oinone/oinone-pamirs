package pro.shushi.pamirs.framework.orm.xml.converter;

import com.thoughtworks.xstream.converters.SingleValueConverter;
import org.apache.dubbo.common.utils.CollectionUtils;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * XML分号表达式转换器
 *
 * @author Adamancy Zhang at 23:21 on 2024-07-25
 */
public class SemicolonSingleValueConverter implements SingleValueConverter {

    @Override
    public Object fromString(String str) {
        if (null == str) {
            return null;
        }
        String[] parts = str.split(CharacterConstants.SEPARATOR_SEMICOLON);
        if (0 == parts.length) {
            return null;
        }
        return Arrays.stream(parts).map(String::trim).collect(Collectors.toList());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public String toString(Object list) {
        if (null == list) {
            return null;
        }
        return CollectionUtils.join((List) list, CharacterConstants.SEPARATOR_SEMICOLON);
    }

    @Override
    public boolean canConvert(Class type) {
        return false;
    }

}