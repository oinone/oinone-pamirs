package pro.shushi.pamirs.meta.api.core.configure.yaml.data;

import pro.shushi.pamirs.meta.api.core.configure.yaml.data.model.PamirsPersistenceItemConfiguration;

/**
 * 持久层配置代理
 * <p>
 * 2020/6/22 8:37 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public interface PamirsPersistenceConfigurationProxy {

    PamirsPersistenceItemConfiguration fetchPamirsPersistenceConfiguration(String dsKey);

    boolean isSharding(String module, String model);

}
