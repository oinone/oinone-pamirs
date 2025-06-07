package pro.shushi.pamirs.framework.orm.xml.provider;

import com.thoughtworks.xstream.converters.javabean.NativePropertySorter;
import com.thoughtworks.xstream.converters.javabean.PropertyDictionary;
import com.thoughtworks.xstream.converters.javabean.PropertySorter;
import com.thoughtworks.xstream.converters.reflection.MissingFieldException;
import com.thoughtworks.xstream.converters.reflection.ObjectAccessException;
import com.thoughtworks.xstream.core.Caching;
import com.thoughtworks.xstream.core.util.OrderRetainingMap;
import pro.shushi.pamirs.framework.orm.xml.bean.ChainIntrospector;

import java.beans.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Pamirs属性字典
 * 2022/3/16 5:00 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class PamirsPropertyDictionary extends PropertyDictionary implements Caching {

    private transient Map propertyNameCache;
    private final PropertySorter sorter;

    public PamirsPropertyDictionary() {
        this(new NativePropertySorter());
    }

    public PamirsPropertyDictionary(PropertySorter sorter) {
        this.propertyNameCache = Collections.synchronizedMap(new HashMap());
        this.sorter = sorter;
    }

    public Iterator propertiesFor(Class type) {
        return this.buildMap(type).values().iterator();
    }

    public PropertyDescriptor propertyDescriptor(Class type, String name) {
        PropertyDescriptor descriptor = this.propertyDescriptorOrNull(type, name);
        if (descriptor == null) {
            throw new MissingFieldException(type.getName(), name);
        } else {
            return descriptor;
        }
    }

    public PropertyDescriptor propertyDescriptorOrNull(Class type, String name) {
        return (PropertyDescriptor) this.buildMap(type).get(name);
    }

    private Map buildMap(Class type) {
        Map nameMap = (Map) this.propertyNameCache.get(type);
        if (nameMap == null) {
            BeanInfo beanInfo;
            try {
                beanInfo = Introspector.getBeanInfo(type, Object.class);
            } catch (IntrospectionException var7) {
                ObjectAccessException oaex = new ObjectAccessException("Cannot get BeanInfo of type", var7);
                oaex.add("bean-type", type.getName());
                throw oaex;
            }

            nameMap = new OrderRetainingMap();
            MethodDescriptor[] methodDescriptors = beanInfo.getMethodDescriptors();
            PropertyDescriptor[] propertyDescriptors = ChainIntrospector.getTargetPropertyInfo(type, methodDescriptors);

            for (int i = 0; i < propertyDescriptors.length; ++i) {
                PropertyDescriptor descriptor = propertyDescriptors[i];
                nameMap.put(descriptor.getName(), descriptor);
            }

            nameMap = this.sorter.sort(type, nameMap);
            this.propertyNameCache.put(type, nameMap);
        }

        return nameMap;
    }

    public void flushCache() {
        this.propertyNameCache.clear();
    }

}
