package pro.shushi.pamirs.framework.common.utils.xstream;

import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;
import pro.shushi.pamirs.framework.common.entry.TreeNode;

/**
 * {@link TreeNode}映射包装类
 *
 * @author Adamancy Zhang at 12:19 on 2021-08-04
 */
public class TreeNodeMapperWrapper extends MapperWrapper {

    public TreeNodeMapperWrapper(Mapper wrapped) {
        super(wrapped);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Class realClass(String elementName) {
        return TreeNode.class;
    }
}
