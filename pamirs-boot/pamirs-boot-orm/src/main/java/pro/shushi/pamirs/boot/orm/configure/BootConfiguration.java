package pro.shushi.pamirs.boot.orm.configure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleOptions;
import pro.shushi.pamirs.boot.common.api.contants.InstallEnum;
import pro.shushi.pamirs.boot.common.api.contants.ProfileEnum;
import pro.shushi.pamirs.boot.common.api.contants.UpgradeEnum;
import pro.shushi.pamirs.framework.common.constants.ConfigureConstants;
import pro.shushi.pamirs.framework.common.emnu.BootModeEnum;
import pro.shushi.pamirs.meta.annotation.fun.Data;

import java.util.HashSet;
import java.util.Set;

/**
 * 启动配置
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-09 00:22
 */
@Data
@Configuration
@ConfigurationProperties(prefix = ConfigureConstants.PAMIRS_BOOT_CONFIG_PREFIX)
@RefreshScope
public class BootConfiguration {

    // 启动加载程序，是否启动元数据、业务数据和基础设施的加载与更新程序，在应用启动时同时对模块进行生命周期管理
    private Boolean init = Boolean.TRUE;

    // 同步执行加载程序，启动时对模块进行生命周期管理采用同步方式
    private boolean sync = Boolean.TRUE;

    // 模式
    private BootModeEnum mode;

    // 启动模块列表
    private Set<String> modules = new HashSet<>();

    // 排除启动模块列表
    private Set<String> excludeModules = new HashSet<>();

    // 分布式模块列表
    private Set<String> distributionModules = new HashSet<>();

    // 无代码模块列表
    private NoCodeModuleConfiguration noCodeModule = new NoCodeModuleConfiguration();

    // 自动安装，启动模块列表中存在未安装模块，是否自动安装
    private InstallEnum install = InstallEnum.AUTO;

    // 升级 auto | force | readonly
    // auto 自动升级-若模块版本号变高，则进行升级
    // force 强制升级-无论模块版本号有无变化，都进行升级
    // readonly 只读
    private UpgradeEnum upgrade = UpgradeEnum.AUTO;

    // 可选项配置组
    private ProfileEnum profile = ProfileEnum.CUSTOMIZE;

    // 生命周期管理可选项
    private AppLifecycleOptions options;

}
