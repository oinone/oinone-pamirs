package pro.shushi.pamirs.framework.connectors.data.infrastructure.test.mock.mapper;

import org.apache.ibatis.annotations.Mapper;
import pro.shushi.pamirs.framework.connectors.data.infrastructure.test.mock.model.SchemaPlayBack;
import pro.shushi.pamirs.framework.connectors.data.mapper.PamirsMapper;

/**
 * 系统表信息回放mapper
 *
 * 2020/6/29 2:02 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Mapper
public interface SchemaPlayBackMapper extends PamirsMapper<SchemaPlayBack> {

}
