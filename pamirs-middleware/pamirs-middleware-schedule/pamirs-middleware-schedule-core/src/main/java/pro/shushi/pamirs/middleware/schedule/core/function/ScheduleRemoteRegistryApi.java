package pro.shushi.pamirs.middleware.schedule.core.function;

import org.apache.dubbo.rpc.service.GenericService;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;
import pro.shushi.pamirs.middleware.schedule.core.function.model.FunctionDefinition;

/**
 * 远程调用注册Api
 *
 * @author cpc
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface ScheduleRemoteRegistryApi {

    /**
     * 注册远程服务消费者
     *
     * @param functionDefinition 函数定义
     * @return 消费者
     */
    GenericService registryConsumer(FunctionDefinition<?> functionDefinition);

    /**
     * 获取远程服务的方法名称
     *
     * @param functionDefinition 函数定义
     * @return method
     */
    String getGenericServiceMethodName(FunctionDefinition<?> functionDefinition);

    /**
     * 获取远程服务的方法的参数类型
     *
     * @param functionDefinition 函数定义
     * @return method
     */
    String[] getParameterTypes(FunctionDefinition<?> functionDefinition);
}
