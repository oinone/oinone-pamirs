package pro.shushi.pamirs.framework.gateways.rsql.optimizer;

import pro.shushi.pamirs.framework.gateways.rsql.RSQLNodeInfo;
import pro.shushi.pamirs.framework.gateways.rsql.RSQLNodeInfoType;

import java.util.List;

/**
 * RSQL节点优化器
 *
 * @author Adamancy Zhang at 19:43 on 2024-09-18
 */
public class RSQLNodeOptimizer implements NodeOptimizer {

    public static final RSQLNodeOptimizer INSTANCE = new RSQLNodeOptimizer();

    @Override
    public String logicOptimizer(RSQLNodeInfoType type, List<String> values) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String comparisonOptimizer(RSQLNodeInfo nodeInfo) {
        throw new UnsupportedOperationException();
    }
}
