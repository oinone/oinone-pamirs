package pro.shushi.pamirs.framework.connectors.data.mapper.method.gauss;


import pro.shushi.pamirs.framework.connectors.data.mapper.method.api.DeleteMethod;
import pro.shushi.pamirs.framework.connectors.data.mapper.method.gauss.constants.GaussScriptTemplate;
import pro.shushi.pamirs.framework.connectors.data.mapper.template.SqlTemplate;
import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.model.PamirsTableInfo;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;

/**
 * 通用删除SQL Statement 方法
 *
 * @author paidaxing
 * @version 1.0.0
 * @date 2024/03/15 10:03:26
 */
@Data
public class GaussDeleteMethod  extends GaussSelectMethod implements DeleteMethod {
    public GaussDeleteMethod(ModelConfig modelConfig){
        super(modelConfig);
    }
    @Override
    public String sqlDelete() {
        PamirsTableInfo pamirsTableInfo = PamirsTableInfo.fetchPamirsTableInfo(getModelConfig().getModel());
        boolean logicDelete = pamirsTableInfo.getLogicDelete();
        if (!logicDelete) {
            //删除
            return SqlTemplate.DELETE;
        } else {
            //逻辑删除，修改状态为未启用
            return GaussScriptTemplate.LOGIC_DELETE_SQL;
        }
    }
}
