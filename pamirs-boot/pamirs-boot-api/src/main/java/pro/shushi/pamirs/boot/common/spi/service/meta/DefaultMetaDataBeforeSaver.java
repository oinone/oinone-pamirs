package pro.shushi.pamirs.boot.common.spi.service.meta;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.api.init.SystemBootDataInit;
import pro.shushi.pamirs.boot.common.spi.api.meta.MetaDataBeforeSaverApi;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.util.List;

/**
 * 元数据存储前置API
 * <p>
 * 2020/8/27 5:53 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Slf4j
@Order
@Component
@SPI.Service
public class DefaultMetaDataBeforeSaver implements MetaDataBeforeSaverApi {

    @Override
    public void before(AppLifecycleCommand command, List<SystemBootDataInit> systemBootBeforeInits) {
        if (!command.getOptions().isUpdateMeta()) {
            return;
        }
        // 系统初始化数据前置处理
        for (SystemBootDataInit systemBootDataInit : systemBootBeforeInits) {
            long start = System.currentTimeMillis();
            systemBootDataInit.init(command);
            log.info("{} system boot after cost time: {}ms", systemBootDataInit.getClass().getName(), System.currentTimeMillis() - start);
        }
    }

}
