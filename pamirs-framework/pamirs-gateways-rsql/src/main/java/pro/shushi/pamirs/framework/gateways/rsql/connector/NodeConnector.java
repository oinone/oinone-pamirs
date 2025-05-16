package pro.shushi.pamirs.framework.gateways.rsql.connector;

import pro.shushi.pamirs.framework.common.entry.TreeNode;
import pro.shushi.pamirs.framework.gateways.rsql.RSQLNodeInfo;
import pro.shushi.pamirs.framework.gateways.rsql.RSQLNodeInfoType;

import java.util.List;

/**
 * 节点连接器
 *
 * @author Adamancy Zhang at 14:02 on 2021-10-21
 */
public interface NodeConnector {

    /**
     * 逻辑值连接
     *
     * @return 连接后的字符串
     */
    default String logicConnector(TreeNode<RSQLNodeInfo> node, RSQLNodeInfoType type, List<String> values) {
        return logicConnector(type, values);
    }

    default String comparisonConnector(TreeNode<RSQLNodeInfo> node) {
        return comparisonConnector(node.getValue());
    }

    /**
     * 表达式连接
     *
     * @return 连接后的字符串
     * @deprecated please using {@link NodeConnector#comparisonConnector(TreeNode)}
     */
    @Deprecated
    String comparisonConnector(RSQLNodeInfo nodeInfo);

    /**
     * 逻辑值连接
     *
     * @return 连接后的字符串
     * @deprecated please using {@link NodeConnector#logicConnector(TreeNode, RSQLNodeInfoType, List)}
     */
    @Deprecated
    String logicConnector(RSQLNodeInfoType type, List<String> values);
}
