package pro.shushi.pamirs.boot.common.api.init;

import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.api.core.compute.Prioritized;

/**
 * 每次系统的时候都要执行的数据初始化动作
 */
public interface SystemBootAfterInit extends Prioritized, CommonApi {

    /**
     * 系统启动初始化程序
     *
     * @return 是否初始化成功
     */
    boolean init(AppLifecycleCommand command);

}
