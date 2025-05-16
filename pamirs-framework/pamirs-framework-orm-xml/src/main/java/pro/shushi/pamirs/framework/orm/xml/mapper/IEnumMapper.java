package pro.shushi.pamirs.framework.orm.xml.mapper;

import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.converters.enums.EnumSingleValueConverter;
import com.thoughtworks.xstream.core.Caching;
import com.thoughtworks.xstream.mapper.AttributeMapper;
import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;
import pro.shushi.pamirs.framework.orm.xml.converter.IEnumSingleValueXmlConverter;
import pro.shushi.pamirs.meta.common.enmu.BaseEnum;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * IEnum枚举XML转换器
 * 2022/3/16 4:57 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class IEnumMapper extends MapperWrapper implements Caching {
    private transient AttributeMapper attributeMapper;
    private transient Map<Class, SingleValueConverter> enumConverterMap;
    private transient boolean caseSensitive;

    public IEnumMapper(Mapper wrapped, boolean caseSensitive) {
        super(wrapped);
        this.readResolve();
        this.caseSensitive = caseSensitive;
    }

    public String serializedClass(Class type) {
        if (type == null) {
            return super.serializedClass(type);
        } else if (BaseEnum.class.isAssignableFrom(type)) {
            return super.serializedClass(type.getSuperclass());
        } else {
            return EnumSet.class.isAssignableFrom(type) ? super.serializedClass(EnumSet.class) : super.serializedClass(type);
        }
    }

    public boolean isImmutableValueType(Class type) {
        return BaseEnum.class.isAssignableFrom(type) || super.isImmutableValueType(type);
    }

    public boolean isReferenceable(Class type) {
        return (type == null || !BaseEnum.class.isAssignableFrom(type)) && super.isReferenceable(type);
    }

    public SingleValueConverter getConverterFromItemType(String fieldName, Class type, Class definedIn) {
        SingleValueConverter converter = this.getLocalConverter(fieldName, type, definedIn);
        return converter == null ? super.getConverterFromItemType(fieldName, type, definedIn) : converter;
    }

    public SingleValueConverter getConverterFromAttribute(Class definedIn, String attribute, Class type) {
        SingleValueConverter converter = this.getLocalConverter(attribute, type, definedIn);
        return converter == null ? super.getConverterFromAttribute(definedIn, attribute, type) : converter;
    }

    private SingleValueConverter getLocalConverter(String fieldName, Class type, Class definedIn) {
        if (this.attributeMapper != null && IEnum.class.isAssignableFrom(type) && this.attributeMapper.shouldLookForSingleValueConverter(fieldName, type, definedIn)) {
            synchronized (this.enumConverterMap) {
                SingleValueConverter singleValueConverter = this.enumConverterMap.get(type);
                if (singleValueConverter == null) {
                    singleValueConverter = super.getConverterFromItemType(fieldName, type, definedIn);
                    if (singleValueConverter == null || singleValueConverter instanceof EnumSingleValueConverter) {
                        singleValueConverter = new IEnumSingleValueXmlConverter(type, caseSensitive);
                    }

                    this.enumConverterMap.put(type, singleValueConverter);
                }

                return singleValueConverter;
            }
        } else {
            return null;
        }
    }

    public void flushCache() {
        if (this.enumConverterMap.size() > 0) {
            synchronized (this.enumConverterMap) {
                this.enumConverterMap.clear();
            }
        }

    }

    private Object readResolve() {
        this.enumConverterMap = new HashMap();
        this.attributeMapper = (AttributeMapper) this.lookupMapperOfType(AttributeMapper.class);
        return this;
    }

}
