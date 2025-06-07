package pro.shushi.pamirs.meta.api.core.orm;

import pro.shushi.pamirs.meta.api.enmu.BatchCommitTypeEnum;
import pro.shushi.pamirs.meta.api.enmu.BatchOpTypeEnum;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

import java.util.function.Function;

/**
 * 批量操作接口
 * 2020/12/16 9:26 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface BatchApi {

    default int run(BatchConsumer<Integer> consumer, BatchCommitTypeEnum operation) {
        BatchCommitTypeEnum batchOperation = PamirsSession.getBatchOperation();
        try {
            PamirsSession.setBatchOperation(operation);
            return consumer.consume();
        } finally {
            PamirsSession.setBatchOperation(batchOperation);
        }
    }

    interface BatchConsumer<T> {
        T consume();
    }

    BatchCommitTypeEnum fix(BatchOpTypeEnum opType, String model, BatchCommitTypeEnum operation);

    default int strategy(BatchOpTypeEnum opType, String model, Integer batchSize,
                         Function<Integer, Integer> collectionCommit,
                         Function<Integer, Integer> batchCommit) {
        BatchCommitTypeEnum batchOperation = fix(opType, model, PamirsSession.getBatchOperation());
        PamirsSession.setBatchOperation(batchOperation);
        switch (batchOperation) {
            case collectionCommit:
                return collectionCommit.apply(batchSize);
            case batchCommit:
                return batchCommit.apply(batchSize);
            case useAffectRows:
            case useAndJudgeAffectRows:
                return collectionCommit.apply(1);
            default:
                return 0;
        }
    }

}
