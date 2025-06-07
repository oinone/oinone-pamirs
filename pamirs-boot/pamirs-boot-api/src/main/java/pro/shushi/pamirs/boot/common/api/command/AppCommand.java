package pro.shushi.pamirs.boot.common.api.command;

import com.alibaba.fastjson.annotation.JSONField;
import pro.shushi.pamirs.boot.common.api.contants.InstallEnum;
import pro.shushi.pamirs.boot.common.api.contants.ProfileEnum;
import pro.shushi.pamirs.boot.common.api.contants.UpgradeEnum;

import java.io.Serializable;

/**
 * 生命周期管理指令
 * <p>
 * 2021/2/25 1:50 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class AppCommand implements Serializable {


    private static final long serialVersionUID = 6899424210907236835L;

    // 安装指令
    @JSONField(ordinal = 1)
    private final InstallEnum installEnum;

    // 升级指令
    @JSONField(ordinal = 2)
    private final UpgradeEnum upgradeEnum;

    // 可选项配置组
    @JSONField(ordinal = 3)
    private final ProfileEnum profile;

    public AppCommand(InstallEnum installEnum, UpgradeEnum upgradeEnum, ProfileEnum profile) {
        this.installEnum = installEnum;
        this.upgradeEnum = upgradeEnum;
        this.profile = profile;
    }

    public InstallEnum getInstallEnum() {
        return installEnum;
    }

    public UpgradeEnum getUpgradeEnum() {
        return upgradeEnum;
    }

    public ProfileEnum getProfile() {
        return profile;
    }

}
