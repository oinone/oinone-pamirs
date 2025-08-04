package pro.shushi.pamirs.boot.standard.spi;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleOptions;
import pro.shushi.pamirs.boot.common.api.contants.InstallEnum;
import pro.shushi.pamirs.boot.common.api.contants.ProfileEnum;
import pro.shushi.pamirs.boot.common.api.contants.UpgradeEnum;
import pro.shushi.pamirs.boot.common.spi.api.boot.BootConditionApi;
import pro.shushi.pamirs.boot.orm.configure.BootConfiguration;
import pro.shushi.pamirs.framework.common.emnu.BootModeEnum;
import pro.shushi.pamirs.meta.api.session.cache.spi.SessionFillOwnSignApi;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.Spider;

import jakarta.annotation.Resource;

/**
 * 启动条件实现
 * <p>
 * 2020/8/27 5:05 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Order(66)
@Component
@SPI.Service
public class StandardBootCondition implements BootConditionApi {

    @Resource
    private BootConfiguration bootConfiguration;

    @Override
    public boolean needLoad() {
        return null != bootConfiguration.getInit() && bootConfiguration.getInit();
    }

    @Override
    public boolean isSync() {
        return bootConfiguration.isSync();
    }

    @Override
    public BootModeEnum mode() {
        if (Spider.getDefaultExtension(SessionFillOwnSignApi.class).handleOwnSign()) {
            return null;
        }
        return bootConfiguration.getMode();
    }

    @Override
    public InstallEnum install() {
        return bootConfiguration.getInstall();
    }

    @Override
    public UpgradeEnum upgrade() {
        return bootConfiguration.getUpgrade();
    }

    @Override
    public ProfileEnum profile() {
        return bootConfiguration.getProfile();
    }

    @Override
    public AppLifecycleOptions options() {
        return bootConfiguration.getOptions();
    }

}
