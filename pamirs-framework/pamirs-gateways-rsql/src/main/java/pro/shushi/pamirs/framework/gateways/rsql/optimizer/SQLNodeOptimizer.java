package pro.shushi.pamirs.framework.gateways.rsql.optimizer;

import pro.shushi.pamirs.framework.gateways.rsql.RSQLNodeInfo;
import pro.shushi.pamirs.framework.gateways.rsql.RSQLNodeInfoType;

import java.util.List;

/**
 * SQL节点优化器
 *
 * @author Adamancy Zhang at 19:44 on 2024-09-18
 */
public class SQLNodeOptimizer implements NodeOptimizer {

    public static final SQLNodeOptimizer INSTANCE = new SQLNodeOptimizer();

    @Override
    public String logicOptimizer(RSQLNodeInfoType type, List<String> values) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String comparisonOptimizer(RSQLNodeInfo nodeInfo) {
        throw new UnsupportedOperationException();
    }
}
