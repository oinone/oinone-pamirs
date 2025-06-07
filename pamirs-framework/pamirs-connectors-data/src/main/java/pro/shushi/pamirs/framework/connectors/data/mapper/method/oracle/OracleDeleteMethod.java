package pro.shushi.pamirs.framework.connectors.data.mapper.method.oracle;

import pro.shushi.pamirs.framework.connectors.data.mapper.method.api.DeleteMethod;
import pro.shushi.pamirs.framework.connectors.data.mapper.method.oracle.constants.OracleScriptTemplate;
import pro.shushi.pamirs.framework.connectors.data.mapper.template.SqlTemplate;
import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.model.PamirsTableInfo;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;

/**
 * 通用删除 SQL statement 生成方法
 * <p>
 * 2020/6/16 1:40 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Data
public class OracleDeleteMethod extends OracleSelectMethod implements DeleteMethod {

    public OracleDeleteMethod(ModelConfig modelConfig) {
        super(modelConfig);
    }

    @Override
    public String sqlDelete() {
        PamirsTableInfo pamirsTableInfo = PamirsTableInfo.fetchPamirsTableInfo(getModelConfig().getModel());
        boolean logicDelete = pamirsTableInfo.getLogicDelete();
        if (!logicDelete) {
            return SqlTemplate.DELETE;
        } else {
            return OracleScriptTemplate.LOGIC_DELETE_SQL;
        }
    }

}
