package pro.shushi.pamirs.framework.gateways.rsql.computer;

import pro.shushi.pamirs.framework.gateways.rsql.RSQLNodeInfo;

/**
 * 节点计算器
 *
 * @author Adamancy Zhang at 12:43 on 2024-08-01
 */
public interface NodeComputer<T> {

    /**
     * 表达式计算
     *
     * @param nodeInfo 节点信息
     * @param data     数据
     * @return 计算结果
     */
    boolean comparisonCompute(RSQLNodeInfo nodeInfo, T data);
}
