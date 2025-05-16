package pro.shushi.pamirs.middleware.schedule.core.function;

import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;
import pro.shushi.pamirs.middleware.schedule.core.function.model.FunctionDefinition;

/**
 * Schedule远程调用API
 *
 * @author Adamancy Zhang at 15:05 on 2024-08-27
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface ScheduleRemoteInvokeApi {

    /**
     * consumer request
     *
     * @param functionDefinition 函数定义
     * @param args               请求参数
     * @return 处理后的响应结果
     */
    Object invoke(FunctionDefinition<?> functionDefinition, Object[] args);

}
