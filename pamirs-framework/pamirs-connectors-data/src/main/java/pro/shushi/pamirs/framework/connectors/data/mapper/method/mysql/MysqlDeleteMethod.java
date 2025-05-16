package pro.shushi.pamirs.framework.connectors.data.mapper.method.mysql;

import pro.shushi.pamirs.framework.connectors.data.mapper.method.api.DeleteMethod;
import pro.shushi.pamirs.framework.connectors.data.mapper.method.mysql.constants.MysqlScriptTemplate;
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
public class MysqlDeleteMethod extends MysqlSelectMethod implements DeleteMethod {

    public MysqlDeleteMethod(ModelConfig modelConfig) {
        super(modelConfig);
    }

    @Override
    public String sqlDelete() {
        PamirsTableInfo pamirsTableInfo = PamirsTableInfo.fetchPamirsTableInfo(getModelConfig().getModel());
        boolean logicDelete = pamirsTableInfo.getLogicDelete();
        if (!logicDelete) {
            return SqlTemplate.DELETE;
        } else {
            return MysqlScriptTemplate.LOGIC_DELETE_SQL;
        }
    }

}
