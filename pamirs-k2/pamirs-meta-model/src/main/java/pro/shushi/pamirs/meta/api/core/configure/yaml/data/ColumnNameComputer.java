package pro.shushi.pamirs.meta.api.core.configure.yaml.data;

import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelField;

import java.util.Map;

/**
 * 列名计算自定义参数提供接口
 *
 * @author Adamancy Zhang at 21:03 on 2025-07-11
 */
@SPI
public interface ColumnNameComputer {

    Map<String, Object> context(ModelDefinition modelDefinition, ModelField modelField);

}
