package pro.shushi.pamirs.meta.api.core.configure.yaml.data;

import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;

import java.util.Map;

/**
 * 表名计算自定义参数提供接口
 * <p>
 * 2020/6/22 9:11 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SPI
public interface TableNameComputer {

    Map<String, Object> context(ModelDefinition modelDefinition);

}
