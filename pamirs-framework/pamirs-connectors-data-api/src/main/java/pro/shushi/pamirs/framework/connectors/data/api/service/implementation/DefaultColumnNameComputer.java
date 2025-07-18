package pro.shushi.pamirs.framework.connectors.data.api.service.implementation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.ColumnNameComputer;
import pro.shushi.pamirs.meta.common.constants.VariableNameConstants;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.util.PStringUtils;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelField;

import java.util.HashMap;
import java.util.Map;

/**
 * 列名计算自定义参数提供接口默认实现
 *
 * @author Adamancy Zhang at 21:04 on 2025-07-11
 */
@Order
@SPI.Service
public class DefaultColumnNameComputer implements ColumnNameComputer {

    @Override
    public Map<String, Object> context(ModelDefinition modelDefinition, ModelField modelField) {
        Map<String, Object> context = new HashMap<>();
        context.put(VariableNameConstants.column30, generatorColumn30(modelField));
        return context;
    }

    private String generatorColumn30(ModelField modelField) {
        String column = modelField.getColumn();
        if (StringUtils.isBlank(column)) {
            String name = modelField.getName();
            column = PStringUtils.fieldName2Column(name);
        }
        return DefaultTableNameComputer.format(column);
    }
}
