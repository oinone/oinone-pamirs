package pro.shushi.pamirs.framework.gateways.rsql.connector;

import java.util.function.Supplier;

/**
 * 节点连接器类型
 *
 * @author Adamancy Zhang at 14:09 on 2021-10-21
 */
public enum NodeConnectorType {

    RSQL(() -> RSQLNodeConnector.INSTANCE),

    SQL(() -> SQLNodeConnector.INSTANCE);

    private final Supplier<NodeConnector> supplier;

    NodeConnectorType(Supplier<NodeConnector> supplier) {
        this.supplier = supplier;
    }

    public NodeConnector instance() {
        return supplier.get();
    }
}
