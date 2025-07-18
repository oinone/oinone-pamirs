package pro.shushi.pamirs.framework.connectors.data.api.service.implementation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.TableNameComputer;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
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

    private static final int ORACLE_DB_IDENTIFIER_LENGTH = 30;

    @Override
    public Map<String, Object> context(ModelDefinition modelDefinition) {
        Map<String, Object> context = new HashMap<>();
        String module = modelDefinition.getModule();
        String moduleAbbr = Optional.ofNullable(modelDefinition.getModuleAbbr())
                .filter(StringUtils::isNotBlank).orElse(ModelUtils.moduleAbbreviate(module));
        context.put(VariableNameConstants.module, module);
        context.put(VariableNameConstants.moduleAbbr, moduleAbbr);
        context.put(VariableNameConstants.table30, generatorTable30(modelDefinition));
        return context;
    }

    private String generatorTable30(ModelDefinition modelDefinition) {
        String table = modelDefinition.getTable();
        if (StringUtils.isBlank(table)) {
            String name = modelDefinition.getName();
            table = PStringUtils.fieldName2Column(name);
        }
        return format(table);
    }

    public static String format(String table) {
        int l = table.length();
        if (l <= ORACLE_DB_IDENTIFIER_LENGTH) {
            return table;
        }
        String[] columnWords = table.split(CharacterConstants.SEPARATOR_UNDERLINE);
        for (int i = 0; i < columnWords.length; i++) {
            l -= columnWords[i].length() - 1;
            columnWords[i] = columnWords[i].substring(0, 1);
            if (l <= ORACLE_DB_IDENTIFIER_LENGTH) {
                return String.join("_", columnWords);
            }
        }
        return table;
    }
}
