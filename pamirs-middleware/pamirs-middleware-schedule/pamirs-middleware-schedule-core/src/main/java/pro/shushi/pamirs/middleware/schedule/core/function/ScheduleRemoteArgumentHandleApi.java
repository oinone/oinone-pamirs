package pro.shushi.pamirs.middleware.schedule.core.function;

import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;
import pro.shushi.pamirs.middleware.schedule.core.function.model.FunctionDefinition;

/**
 * Schedule远程调用参数处理API
 *
 * @author Adamancy Zhang at 14:27 on 2024-08-27
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface ScheduleRemoteArgumentHandleApi {

    /**
     * consumer request before handle
     *
     * @param functionDefinition 函数定义
     * @param args               请求参数
     * @return 处理后的请求参数
     */
    Object[] requestHandle(FunctionDefinition<?> functionDefinition, Object[] args);

    /**
     * consumer response after handle
     *
     * @param functionDefinition 函数定义
     * @param result             响应结果
     * @return 处理后的响应结果
     */
    Object responseHandle(FunctionDefinition<?> functionDefinition, Object result);

}
