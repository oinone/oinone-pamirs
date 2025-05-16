package pro.shushi.pamirs.core.common.xstream;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.core.common.StringHelper;
import pro.shushi.pamirs.framework.common.entry.TreeNode;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * TreeNode&lt;XMLNodeContent&gt;
 *
 * @author Adamancy Zhang at 12:19 on 2021-08-04
 */
public class XStreamTreeNodeConverter implements Converter {

    @Override
    public boolean canConvert(Class aClass) {
        return TreeNode.class.isAssignableFrom(aClass);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void marshal(Object o, HierarchicalStreamWriter hierarchicalStreamWriter, MarshallingContext marshallingContext) {
        hierarchicalStreamWriter.endNode();
        hierarchicalStreamWriter.flush();

        TreeNode<XMLNodeContent> root = (TreeNode<XMLNodeContent>) o;
        hierarchicalStreamWriter.startNode(root.getKey());
        setContentToXML(hierarchicalStreamWriter, root);

        analysisNode(hierarchicalStreamWriter, root.getChildren());
    }

    private void analysisNode(HierarchicalStreamWriter hierarchicalStreamWriter, List<TreeNode<XMLNodeContent>> children) {
        for (TreeNode<XMLNodeContent> child : children) {
            hierarchicalStreamWriter.startNode(child.getKey());
            setContentToXML(hierarchicalStreamWriter, child);
            analysisNode(hierarchicalStreamWriter, child.getChildren());
            hierarchicalStreamWriter.endNode();
        }
    }

    private void setContentToXML(HierarchicalStreamWriter hierarchicalStreamWriter, TreeNode<XMLNodeContent> node) {
        XMLNodeContent rootValue = node.getValue();
        for (Map.Entry<String, String> attribute : rootValue.getAttributes().entrySet()) {
            String value = attribute.getValue();
            if (StringUtils.isNotBlank(value)) {
                hierarchicalStreamWriter.addAttribute(attribute.getKey(), value);
            }
        }
        String content = rootValue.getContent();
        if (StringUtils.isNotBlank(content)) {
            hierarchicalStreamWriter.setValue(content);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object unmarshal(HierarchicalStreamReader hierarchicalStreamReader, UnmarshallingContext unmarshallingContext) {
        return createNode(hierarchicalStreamReader, unmarshallingContext, (TreeNode<XMLNodeContent>) unmarshallingContext.currentObject());
    }

    private TreeNode<XMLNodeContent> createNode(HierarchicalStreamReader hierarchicalStreamReader, UnmarshallingContext unmarshallingContext, TreeNode<XMLNodeContent> parent) {
        String nodeName = hierarchicalStreamReader.getNodeName();
        XMLNodeContent content = new XMLNodeContent();
        Iterator<?> iterator = hierarchicalStreamReader.getAttributeNames();
        while (iterator.hasNext()) {
            String key = StringHelper.valueOf(iterator.next());
            if (CharacterConstants.SEPARATOR_EMPTY.equals(key)) {
                continue;
            }
            String value = hierarchicalStreamReader.getAttribute(key);
            if (StringUtils.isNotBlank(value)) {
                content.putAttribute(key, value);
            }
        }
        String value = hierarchicalStreamReader.getValue();
        if (value != null) {
            value = value.trim();
            if (StringUtils.isNotBlank(value)) {
                content.setContent(value);
            }
        }
        TreeNode<XMLNodeContent> node = new TreeNode<>(nodeName, content, parent);
        while (hierarchicalStreamReader.hasMoreChildren()) {
            hierarchicalStreamReader.moveDown();
            createNode(hierarchicalStreamReader, unmarshallingContext, node);
            hierarchicalStreamReader.moveUp();
        }
        return node;
    }
}
