package pro.shushi.pamirs.framework.connectors.data.mapper.method.dm;

import pro.shushi.pamirs.framework.connectors.data.mapper.method.oracle.OracleUpdateMethod;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;

/**
 * DM Update SQL statement generate method
 *
 * @author Adamancy Zhang at 12:00 on 2023-06-29
 */
public class DMUpdateMethod extends OracleUpdateMethod {

    public DMUpdateMethod(ModelConfig modelConfig) {
        super(modelConfig);
    }
}
