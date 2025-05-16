package pro.shushi.pamirs.framework.orm.xml.converter;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.common.enmu.Enums;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * 枚举XML转换器
 * 2022/3/16 4:57 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class IEnumSingleValueXmlConverter extends AbstractSingleValueConverter {

    private final Class<? extends IEnum<?>> enumType;

    private boolean caseSensitive;

    public IEnumSingleValueXmlConverter(Class<? extends IEnum<?>> enumType, boolean caseSensitive) {
        this.enumType = enumType;
        this.caseSensitive = caseSensitive;
    }

    @Override
    public boolean canConvert(Class type) {
        return IEnum.class.isAssignableFrom(type);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public String toString(Object obj) {
        return ((IEnum) obj).name();
    }

    @Override
    public Object fromString(String s) {
        Object obj = Enums.getEnum(enumType, s);
        if (caseSensitive) {
            return obj;
        }
        if (null != obj) {
            return obj;
        }
        return Enums.getEnum(enumType, StringUtils.swapCase(s));
    }

}