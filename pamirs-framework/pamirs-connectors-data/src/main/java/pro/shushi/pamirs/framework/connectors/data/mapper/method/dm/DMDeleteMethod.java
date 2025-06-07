package pro.shushi.pamirs.framework.connectors.data.mapper.method.dm;

import pro.shushi.pamirs.framework.connectors.data.mapper.method.oracle.OracleDeleteMethod;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;

/**
 * DM Delete SQL statement generate method
 *
 * @author Adamancy Zhang at 11:56 on 2023-06-29
 */
public class DMDeleteMethod extends OracleDeleteMethod {

    public DMDeleteMethod(ModelConfig modelConfig) {
        super(modelConfig);
    }
}
