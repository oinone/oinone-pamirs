package pro.shushi.pamirs.meta.api.core.orm.batch;

import pro.shushi.pamirs.meta.annotation.fun.Data;

@Data
public class BatchSizeWrapper {

    private Integer batchSize;

    private long nanoTime;

    public static BatchSizeWrapper wrap(Integer batchSize) {
        return new BatchSizeWrapper().setBatchSize(batchSize).setNanoTime(System.nanoTime());
    }

}
