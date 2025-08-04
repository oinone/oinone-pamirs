package pro.shushi.pamirs.boot.standard.spi;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.spi.api.boot.BootSystemInitApi;
import pro.shushi.pamirs.boot.common.spi.api.infrastructure.TableBuilderApi;
import pro.shushi.pamirs.boot.common.util.SystemTableHelper;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.Spider;

import jakarta.annotation.Resource;

/**
 * 启动系统初始化接口
 * <p>
 * 2020/8/27 5:03 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Order(66)
@Component
@SPI.Service
public class StandardBootSystemInit implements BootSystemInitApi {

    @Resource
    private SystemTableHelper systemTableHelper;

    @Override
    public void init(AppLifecycleCommand command) {
        boolean diffTable = command.getOptions().isDiffTable();
        boolean rebuildTable = command.getOptions().isRebuildTable();
        if (!rebuildTable) {
            return;
        }
        if (!systemTableHelper.isSystemTableExist()) {
            Spider.getDefaultExtension(TableBuilderApi.class).buildSys(diffTable);
        }
    }


}
