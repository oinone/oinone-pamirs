package pro.shushi.pamirs.framework.connectors.data.mapper.method.dm;

import pro.shushi.pamirs.framework.connectors.data.mapper.method.oracle.OracleSelectMethod;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;

/**
 * DM Select SQL statement generate method
 *
 * @author Adamancy Zhang at 11:59 on 2023-06-29
 */
public class DMSelectMethod extends OracleSelectMethod {

    public DMSelectMethod(ModelConfig modelConfig) {
        super(modelConfig);
    }
}
