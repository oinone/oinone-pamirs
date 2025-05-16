package pro.shushi.pamirs.core.common.xstream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.ConverterRegistry;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.core.ClassLoaderReference;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;

/**
 * 使用{@link AutoIgnoreMapperWrapper}的{@link XStream}扩展
 *
 * @author Adamancy Zhang at 10:36 on 2020-12-25
 */
public class AutoIgnoreXStream extends XStream {

    public AutoIgnoreXStream() {
    }

    public AutoIgnoreXStream(ReflectionProvider reflectionProvider) {
        super(reflectionProvider);
    }

    public AutoIgnoreXStream(HierarchicalStreamDriver hierarchicalStreamDriver) {
        super(hierarchicalStreamDriver);
    }

    public AutoIgnoreXStream(ReflectionProvider reflectionProvider, HierarchicalStreamDriver hierarchicalStreamDriver) {
        super(reflectionProvider, hierarchicalStreamDriver);
    }

    public AutoIgnoreXStream(ReflectionProvider reflectionProvider, HierarchicalStreamDriver driver, ClassLoaderReference classLoaderReference) {
        super(reflectionProvider, driver, classLoaderReference);
    }

    public AutoIgnoreXStream(ReflectionProvider reflectionProvider, HierarchicalStreamDriver driver, ClassLoaderReference classLoaderReference, Mapper mapper) {
        super(reflectionProvider, driver, classLoaderReference, mapper);
    }

    public AutoIgnoreXStream(ReflectionProvider reflectionProvider, HierarchicalStreamDriver driver, ClassLoaderReference classLoaderReference, Mapper mapper, ConverterLookup converterLookup, ConverterRegistry converterRegistry) {
        super(reflectionProvider, driver, classLoaderReference, mapper, converterLookup, converterRegistry);
    }

    @Override
    protected MapperWrapper wrapMapper(MapperWrapper next) {
        return new AutoIgnoreMapperWrapper(next);
    }
}
