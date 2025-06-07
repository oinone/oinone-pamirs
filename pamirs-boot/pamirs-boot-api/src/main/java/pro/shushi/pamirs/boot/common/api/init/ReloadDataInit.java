package pro.shushi.pamirs.boot.common.api.init;

import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.api.core.compute.Prioritized;

import java.util.List;

/**
 * 生命周期重启数据接口
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/15 2:41 下午
 */
public interface ReloadDataInit extends Prioritized, CommonApi {

    /**
     * 初始化程序
     *
     * @param command 生命周期指令
     * @param version 当前安装版本
     * @return 是否初始化成功
     */
    boolean reload(AppLifecycleCommand command, String version);

    /**
     * 生效模块列表
     *
     * @return 触发初始化程序的模块列表
     */
    List<String> modules();

}
