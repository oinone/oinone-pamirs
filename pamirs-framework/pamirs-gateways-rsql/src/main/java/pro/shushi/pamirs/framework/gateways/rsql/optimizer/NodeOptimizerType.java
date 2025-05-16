package pro.shushi.pamirs.framework.gateways.rsql.optimizer;

import java.util.function.Supplier;

/**
 * 节点优化器类型
 *
 * @author Adamancy Zhang at 19:44 on 2024-09-18
 */
public enum NodeOptimizerType {

    RSQL(() -> RSQLNodeOptimizer.INSTANCE),

    SQL(() -> SQLNodeOptimizer.INSTANCE);

    private final Supplier<NodeOptimizer> supplier;

    NodeOptimizerType(Supplier<NodeOptimizer> supplier) {
        this.supplier = supplier;
    }

    public NodeOptimizer instance() {
        return supplier.get();
    }
}
