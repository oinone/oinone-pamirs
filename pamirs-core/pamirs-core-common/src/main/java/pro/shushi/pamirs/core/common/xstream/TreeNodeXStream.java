package pro.shushi.pamirs.core.common.xstream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.ConverterRegistry;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.core.ClassLoaderReference;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.framework.common.entry.TreeNode;

import java.io.*;
import java.net.URL;

/**
 * @deprecated please using {@link pro.shushi.pamirs.framework.common.utils.xstream.TreeNodeXStream}
 */
@Deprecated
public class TreeNodeXStream extends XStream {

    private static final int CLASS_TAG_LENGTH = TreeNode.class.getName().length() + 4;

    private static final XStreamTreeNodeConverter MAP_CONVERTER = new XStreamTreeNodeConverter();

    public TreeNodeXStream() {
        super(null, new PamirsXppDriver());
        initialization();
    }

    public TreeNodeXStream(ReflectionProvider reflectionProvider) {
        super(reflectionProvider, new PamirsXppDriver());
        initialization();
    }

    public TreeNodeXStream(HierarchicalStreamDriver hierarchicalStreamDriver) {
        super(hierarchicalStreamDriver);
        initialization();
    }

    public TreeNodeXStream(ReflectionProvider reflectionProvider, HierarchicalStreamDriver hierarchicalStreamDriver) {
        super(reflectionProvider, hierarchicalStreamDriver);
        initialization();
    }

    @Deprecated
    public TreeNodeXStream(ReflectionProvider reflectionProvider, Mapper mapper, HierarchicalStreamDriver driver) {
        super(reflectionProvider, mapper, driver);
        initialization();
    }

    public TreeNodeXStream(ReflectionProvider reflectionProvider, HierarchicalStreamDriver driver, ClassLoaderReference classLoaderReference) {
        super(reflectionProvider, driver, classLoaderReference);
        initialization();
    }

    @Deprecated
    public TreeNodeXStream(ReflectionProvider reflectionProvider, HierarchicalStreamDriver driver, ClassLoader classLoader) {
        super(reflectionProvider, driver, classLoader);
        initialization();
    }

    @Deprecated
    public TreeNodeXStream(ReflectionProvider reflectionProvider, HierarchicalStreamDriver driver, ClassLoader classLoader, Mapper mapper) {
        super(reflectionProvider, driver, classLoader, mapper);
        initialization();
    }

    public TreeNodeXStream(ReflectionProvider reflectionProvider, HierarchicalStreamDriver driver, ClassLoaderReference classLoaderReference, Mapper mapper) {
        super(reflectionProvider, driver, classLoaderReference, mapper);
        initialization();
    }

    @Deprecated
    public TreeNodeXStream(ReflectionProvider reflectionProvider, HierarchicalStreamDriver driver, ClassLoader classLoader, Mapper mapper, ConverterLookup converterLookup, ConverterRegistry converterRegistry) {
        super(reflectionProvider, driver, classLoader, mapper, converterLookup, converterRegistry);
        initialization();
    }

    public TreeNodeXStream(ReflectionProvider reflectionProvider, HierarchicalStreamDriver driver, ClassLoaderReference classLoaderReference, Mapper mapper, ConverterLookup converterLookup, ConverterRegistry converterRegistry) {
        super(reflectionProvider, driver, classLoaderReference, mapper, converterLookup, converterRegistry);
        initialization();
    }

    @Override
    protected MapperWrapper wrapMapper(MapperWrapper next) {
        return new TreeNodeMapperWrapper(next);
    }

    private void initialization() {
        this.registerConverter(MAP_CONVERTER, PRIORITY_VERY_HIGH);
        this.addImmutableType(TreeNode.class, true);
        XStream.setupDefaultSecurity(this);
        this.allowTypes(new Class[]{TreeNode.class});
    }

    public String toXML(TreeNode<XMLNodeContent> root) {
        String result = super.toXML(root);
        if (StringUtils.isNotBlank(result) && result.length() > CLASS_TAG_LENGTH) {
            result = result.substring(CLASS_TAG_LENGTH);
        }
        return result;
    }

    @Override
    public String toXML(Object obj) {
        return super.toXML(obj);
    }

    @Override
    public void toXML(Object obj, Writer out) {
        super.toXML(obj, out);
    }

    @Override
    public void toXML(Object obj, OutputStream out) {
        super.toXML(obj, out);
    }

    @SuppressWarnings("unchecked")
    @Override
    public TreeNode<XMLNodeContent> fromXML(String xml) {
        return (TreeNode<XMLNodeContent>) super.fromXML(xml);
    }

    @SuppressWarnings("unchecked")
    @Override
    public TreeNode<XMLNodeContent> fromXML(Reader reader) {
        return (TreeNode<XMLNodeContent>) super.fromXML(reader);
    }

    @SuppressWarnings("unchecked")
    @Override
    public TreeNode<XMLNodeContent> fromXML(InputStream input) {
        return (TreeNode<XMLNodeContent>) super.fromXML(input);
    }

    @SuppressWarnings("unchecked")
    @Override
    public TreeNode<XMLNodeContent> fromXML(URL url) {
        return (TreeNode<XMLNodeContent>) super.fromXML(url);
    }

    @SuppressWarnings("unchecked")
    @Override
    public TreeNode<XMLNodeContent> fromXML(File file) {
        return (TreeNode<XMLNodeContent>) super.fromXML(file);
    }
}
