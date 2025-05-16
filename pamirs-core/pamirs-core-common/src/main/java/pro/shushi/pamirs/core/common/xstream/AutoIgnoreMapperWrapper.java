package pro.shushi.pamirs.core.common.xstream;

import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;

/**
 * 自动忽略不在JavaBean中的属性节点
 *
 * @author Adamancy Zhang at 10:37 on 2020-12-25
 */
public class AutoIgnoreMapperWrapper extends MapperWrapper {

    public AutoIgnoreMapperWrapper(Mapper wrapped) {
        super(wrapped);
    }

    @Override
    public boolean shouldSerializeMember(Class definedIn, String fieldName) {
        if (definedIn == Object.class) {
            return false;
        }
        return super.shouldSerializeMember(definedIn, fieldName);
    }
}
