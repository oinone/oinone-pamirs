package pro.shushi.pamirs.trigger.spi;

import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * 远程任务判定
 *
 * @author Adamancy Zhang at 14:48 on 2021-08-10
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface RemoteTaskPredictApi {

    /**
     * 是否指定为远程任务
     *
     * @return 是否强制指定
     */
    boolean isRemote();

    /**
     * 远程任务类型转换
     *
     * @param taskTypeString 当前指定的任务类型字符串
     * @return 远程类型的任务类型字符
     */
    default String converterTaskType(String taskTypeString) {
        return taskTypeString;
    }
}
