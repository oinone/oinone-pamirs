package pro.shushi.pamirs.framework.connectors.data.mapper.method.dm;

import pro.shushi.pamirs.framework.connectors.data.mapper.method.oracle.OracleInsertMethod;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;

/**
 * DM Insert SQL statement generate method
 *
 * @author Adamancy Zhang at 11:58 on 2023-06-29
 */
public class DMInsertMethod extends OracleInsertMethod {

    public DMInsertMethod(ModelConfig modelConfig) {
        super(modelConfig);
    }
}
