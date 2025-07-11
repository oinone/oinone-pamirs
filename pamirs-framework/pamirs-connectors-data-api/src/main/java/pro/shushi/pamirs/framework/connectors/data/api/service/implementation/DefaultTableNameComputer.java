package pro.shushi.pamirs.framework.connectors.data.api.service.implementation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.TableNameComputer;
import pro.shushi.pamirs.meta.common.constants.VariableNameConstants;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.util.PStringUtils;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.util.ModelUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 表名计算自定义参数提供接口默认实现
 * <p>
 * 2020/6/22 9:11 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SuppressWarnings("unused")
@Order
@SPI.Service
public class DefaultTableNameComputer implements TableNameComputer {

    @Override
    public Map<String, Object> context(ModelDefinition modelDefinition) {
        Map<String, Object> context = new HashMap<>();
        String module = modelDefinition.getModule();
        String moduleAbbr = Optional.ofNullable(modelDefinition.getModuleAbbr())
                .filter(StringUtils::isNotBlank).orElse(ModelUtils.moduleAbbreviate(module));
        context.put(VariableNameConstants.module, module);
        context.put(VariableNameConstants.moduleAbbr, moduleAbbr);
        context.put("table_30", generatorTable30(modelDefinition));
        return context;
    }

    private String generatorTable30(ModelDefinition modelDefinition) {
        String name = modelDefinition.getName();
        String table = PStringUtils.fieldName2Column(name);
        return substringTable(table);
    }

    private String substringTable(String table) {
        for (int i = 0; i < 2; i++) {
            if (table.length() <= 30) {
                return table;
            }
            int underlineIndex = table.indexOf("_");
            if (underlineIndex != -1) {
                table = table.substring(underlineIndex + 1);
            }
        }
        return table;
    }
}
