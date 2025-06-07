package pro.shushi.pamirs.boot.common.api.init;

import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.api.core.compute.Prioritized;

import java.util.List;

/**
 * 生命周期升级数据接口
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/15 2:41 下午
 */
public interface UpgradeDataInit extends Prioritized, CommonApi {

    /**
     * 升级程序
     *
     * @param command      生命周期指令
     * @param version      当前升级版本
     * @param existVersion 已安装版本
     * @return 是否初始化成功
     */
    boolean upgrade(AppLifecycleCommand command, String version, String existVersion);

    /**
     * 生效模块列表
     *
     * @return 触发升级程序的模块列表
     */
    List<String> modules();

}
