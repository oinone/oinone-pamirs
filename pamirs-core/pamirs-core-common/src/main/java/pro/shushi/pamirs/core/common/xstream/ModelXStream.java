package pro.shushi.pamirs.core.common.xstream;

import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.ConverterRegistry;
import com.thoughtworks.xstream.core.ClassLoaderReference;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.mapper.Mapper;

/**
 * Model XStream
 *
 * @author Adamancy Zhang at 17:18 on 2021-08-18
 */
public class ModelXStream extends AutoIgnoreXStream {

    public ModelXStream() {
        super(new ModelReflectionProvider());
        init();
    }

    public ModelXStream(HierarchicalStreamDriver hierarchicalStreamDriver) {
        super(new ModelReflectionProvider(), hierarchicalStreamDriver);
        init();
    }

    public ModelXStream(HierarchicalStreamDriver driver, ClassLoaderReference classLoaderReference) {
        super(new ModelReflectionProvider(), driver, classLoaderReference);
        init();
    }

    public ModelXStream(HierarchicalStreamDriver driver, ClassLoaderReference classLoaderReference, Mapper mapper) {
        super(new ModelReflectionProvider(), driver, classLoaderReference, mapper);
        init();
    }

    public ModelXStream(HierarchicalStreamDriver driver, ClassLoaderReference classLoaderReference, Mapper mapper, ConverterLookup converterLookup, ConverterRegistry converterRegistry) {
        super(new ModelReflectionProvider(), driver, classLoaderReference, mapper, converterLookup, converterRegistry);
        init();
    }

    public ModelXStream(ModelReflectionProvider reflectionProvider) {
        super(reflectionProvider);
        init();
    }

    public ModelXStream(ModelReflectionProvider reflectionProvider, HierarchicalStreamDriver hierarchicalStreamDriver) {
        super(reflectionProvider, hierarchicalStreamDriver);
        init();
    }

    public ModelXStream(ModelReflectionProvider reflectionProvider, HierarchicalStreamDriver driver, ClassLoaderReference classLoaderReference) {
        super(reflectionProvider, driver, classLoaderReference);
        init();
    }

    public ModelXStream(ModelReflectionProvider reflectionProvider, HierarchicalStreamDriver driver, ClassLoaderReference classLoaderReference, Mapper mapper) {
        super(reflectionProvider, driver, classLoaderReference, mapper);
        init();
    }

    public ModelXStream(ModelReflectionProvider reflectionProvider, HierarchicalStreamDriver driver, ClassLoaderReference classLoaderReference, Mapper mapper, ConverterLookup converterLookup, ConverterRegistry converterRegistry) {
        super(reflectionProvider, driver, classLoaderReference, mapper, converterLookup, converterRegistry);
        init();
    }

    private void init() {
        registerConverter(EnumerationConverter.INSTANCE, PRIORITY_VERY_HIGH);
    }
}
