package pro.shushi.pamirs.framework.connectors.data.api.orm;

import pro.shushi.pamirs.meta.api.session.PamirsSession;

/**
 * 强制指定查询批量数量
 * <p>
 * 2024/01/04 10:42 上午
 *
 * @author wangxian@shushi.pro
 * @version 1.0.0
 */
public class BatchSizeHintApi implements AutoCloseable {

    /**
     * batchSize = -1 情况下查询数据不进行count(1)的操作
     * @param batchSize
     * @return
     */
    public static BatchSizeHintApi use(Integer batchSize) {
        return new BatchSizeHintApi(batchSize);
    }

    public BatchSizeHintApi(Integer batchSize) {
        PamirsSession.pushBatchSize(batchSize);
    }

    @Override
    public void close() {
        PamirsSession.clearBatchSize();
    }

}
