package pro.shushi.pamirs.framework.gateways.rsql.optimizer;

import pro.shushi.pamirs.framework.gateways.rsql.RSQLNodeInfo;
import pro.shushi.pamirs.framework.gateways.rsql.RSQLNodeInfoType;

import java.util.List;

/**
 * 节点优化器
 *
 * @author Adamancy Zhang at 19:18 on 2024-09-18
 */
public interface NodeOptimizer {

    /**
     * 逻辑值优化
     *
     * @return 优化后的字符串
     */
    String logicOptimizer(RSQLNodeInfoType type, List<String> values);

    /**
     * 表达式优化
     *
     * @return 优化后的字符串
     */
    String comparisonOptimizer(RSQLNodeInfo nodeInfo);
}
