package pro.shushi.pamirs.framework.configure.db.mapper;

import org.apache.ibatis.annotations.Mapper;
import pro.shushi.pamirs.framework.configure.db.model.HookStatic;
import pro.shushi.pamirs.framework.connectors.data.mapper.PamirsMapper;

/**
 * 拦截器mapper
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-09 00:22
 */
@Mapper
public interface HookMapper extends PamirsMapper<HookStatic> {

}
