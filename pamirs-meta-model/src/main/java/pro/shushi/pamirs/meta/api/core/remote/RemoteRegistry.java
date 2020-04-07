package pro.shushi.pamirs.meta.api.core.remote;

import org.apache.dubbo.rpc.service.GenericService;

/**
 * 默认远程服务注册接口
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
public interface RemoteRegistry {

    GenericService registryConsumer(String serviceScope, String group, String version, Integer timeout);

    void registryService(String serviceScope, String methodName, String group, String version, Integer timeout);

}
