package pro.shushi.pamirs.meta.api.core.configure.yaml.data;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.common.constants.VariableNameConstants;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.util.ModelUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

    default Map<String, Object> context(ModelDefinition modelDefinition) {
        Map<String, Object> context = new HashMap<>();
        String module = modelDefinition.getModule();
        String moduleAbbr = Optional.ofNullable(modelDefinition.getModuleAbbr())
                .filter(StringUtils::isNotBlank).orElse(ModelUtils.moduleAbbreviate(module));
        context.put(VariableNameConstants.module, module);
        context.put(VariableNameConstants.moduleAbbr, moduleAbbr);
        return context;
    }

}
