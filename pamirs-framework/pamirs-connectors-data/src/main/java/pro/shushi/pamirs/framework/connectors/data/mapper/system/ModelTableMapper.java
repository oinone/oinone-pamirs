package pro.shushi.pamirs.framework.connectors.data.mapper.system;

import org.apache.ibatis.annotations.Mapper;
import pro.shushi.pamirs.framework.connectors.data.api.domain.model.system.ModelTable;
import pro.shushi.pamirs.framework.connectors.data.mapper.PamirsMapper;

/**
 * 表模型关系mapper
 * <p>
 * 2020/8/8 5:48 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Mapper
public interface ModelTableMapper extends PamirsMapper<ModelTable> {

}
