package pro.shushi.pamirs.boot.common.spi.api.boot;

import pro.shushi.pamirs.boot.common.api.command.AppLifecycleOptions;
import pro.shushi.pamirs.boot.common.api.contants.InstallEnum;
import pro.shushi.pamirs.boot.common.api.contants.ProfileEnum;
import pro.shushi.pamirs.boot.common.api.contants.UpgradeEnum;
import pro.shushi.pamirs.framework.common.emnu.BootModeEnum;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

/**
 * 启动配置接口
 * <p>
 * 2020/8/27 5:03 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface BootConditionApi {

    default boolean needLoad() {
        return true;
    }

    default boolean isSync() {
        return false;
    }

    default BootModeEnum mode() {
        return BootModeEnum.product;
    }

    default InstallEnum install() {
        return InstallEnum.AUTO;
    }

    default UpgradeEnum upgrade() {
        return UpgradeEnum.AUTO;
    }

    default ProfileEnum profile() {
        return ProfileEnum.CUSTOMIZE;
    }

    default AppLifecycleOptions options() {
        return new AppLifecycleOptions();
    }

}
