package pro.shushi.pamirs.meta.api.core.init;

import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.api.core.compute.Prioritized;

/**
 * 生命周期安装数据接口
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/15 2:41 下午
 */
public interface InstallDataInit extends Prioritized, CommonApi {

    void init(String version);

}
