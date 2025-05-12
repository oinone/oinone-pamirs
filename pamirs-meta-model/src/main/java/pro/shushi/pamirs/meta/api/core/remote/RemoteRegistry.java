package pro.shushi.pamirs.meta.api.core.remote;

import org.apache.dubbo.rpc.service.GenericService;
import pro.shushi.pamirs.meta.api.dto.fun.Function;

/**
 * 远程函数服务注册接口
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
public interface RemoteRegistry {

    /**
     * 注册远程服务消费者
     *
     * @param function 函数配置
     * @return 消费者
     */
    GenericService registryConsumer(Function function);

    /**
     * 注册原始远程服务消费者（不使用ownSign注册）
     *
     * @param function 函数配置
     * @return 消费者
     */
    GenericService registryOriginConsumer(Function function);

    /**
     * 注册远程函数服务
     *
     * @param function 函数配置
     */
    void registryService(Function function);

}
