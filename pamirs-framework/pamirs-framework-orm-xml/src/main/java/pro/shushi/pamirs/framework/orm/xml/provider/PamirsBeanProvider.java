package pro.shushi.pamirs.framework.orm.xml.provider;

import com.thoughtworks.xstream.converters.javabean.BeanProvider;
import com.thoughtworks.xstream.converters.javabean.ComparingPropertySorter;
import com.thoughtworks.xstream.converters.javabean.NativePropertySorter;
import com.thoughtworks.xstream.converters.javabean.PropertyDictionary;

import java.util.Comparator;

/**
 * Pamirs bean 提供者
 * 2022/3/16 4:57 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class PamirsBeanProvider extends BeanProvider {

    public PamirsBeanProvider() {
        this(new PamirsPropertyDictionary(new NativePropertySorter()));
    }

    @SuppressWarnings("rawtypes")
    public PamirsBeanProvider(Comparator propertyNameComparator) {
        this(new PamirsPropertyDictionary(new ComparingPropertySorter(propertyNameComparator)));
    }

    public PamirsBeanProvider(PropertyDictionary propertyDictionary) {
        this.propertyDictionary = propertyDictionary;
    }

}
