package pro.shushi.pamirs.meta.api.core.lifecycle;

import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.api.dto.common.Result;

/**
 * 生命周期安装引擎接口
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/15 2:41 下午
 */
public interface LifecycleInstall extends CommonApi {

    /**
     * 安装
     *
     * @return
     */
    Result<Void> install();

}
