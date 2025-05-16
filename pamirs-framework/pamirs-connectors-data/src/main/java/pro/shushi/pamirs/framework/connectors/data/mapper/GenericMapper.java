package pro.shushi.pamirs.framework.connectors.data.mapper;

import org.apache.ibatis.annotations.Mapper;
import pro.shushi.pamirs.meta.api.dto.entity.DataMap;

/**
 * 泛化 mapper
 * <p>
 * 2020/6/29 2:04 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Mapper
public interface GenericMapper extends PamirsMapper<DataMap> {

}
