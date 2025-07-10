package pro.shushi.pamirs.core.common.xstream;

import com.thoughtworks.xstream.mapper.Mapper;

/**
 * @deprecated please using {@link pro.shushi.pamirs.framework.common.utils.xstream.TreeNodeMapperWrapper}
 */
@Deprecated
public class TreeNodeMapperWrapper extends pro.shushi.pamirs.framework.common.utils.xstream.TreeNodeMapperWrapper {

    public TreeNodeMapperWrapper(Mapper wrapped) {
        super(wrapped);
    }
}
