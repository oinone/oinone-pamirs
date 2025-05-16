package pro.shushi.pamirs.framework.configure.db.mapper;

import org.apache.ibatis.annotations.Mapper;
import pro.shushi.pamirs.framework.configure.db.model.FunctionDefinitionStatic;
import pro.shushi.pamirs.framework.connectors.data.mapper.PamirsMapper;

/**
 * 函数定义mapper
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-09 00:22
 */
@Mapper
public interface FunctionDefinitionMapper extends PamirsMapper<FunctionDefinitionStatic> {

}
