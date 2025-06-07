package pro.shushi.pamirs.core.common.xstream;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import pro.shushi.pamirs.core.common.StringHelper;
import pro.shushi.pamirs.meta.common.enmu.Enums;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * 枚举转换器
 *
 * @author Adamancy Zhang at 16:27 on 2021-08-26
 */
public class EnumerationConverter implements Converter {

    public static final EnumerationConverter INSTANCE = new EnumerationConverter();

    private EnumerationConverter() {
        //reject create object
    }

    @Override
    public boolean canConvert(Class type) {
        return IEnum.class.isAssignableFrom(type);
    }

    @Override
    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        writer.setValue(StringHelper.valueOf(((IEnum<?>) source).name()));
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        return Enums.getEnum(context.getRequiredType(), reader.getValue());
    }
}
