package pro.shushi.pamirs.boot.common.spi.service.meta;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.api.init.SystemBootAfterInit;
import pro.shushi.pamirs.boot.common.spi.api.meta.MetaDataAfterSaverApi;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.util.List;

/**
 * 元数据存储后置API
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
public class DefaultMetaDataAfterSaver implements MetaDataAfterSaverApi {

    @Override
    public void after(AppLifecycleCommand command, List<SystemBootAfterInit> systemBootAfterInits) {
        // 系统初始化数据后置处理
        for (SystemBootAfterInit systemBootAfterInit : systemBootAfterInits) {
            long start = System.currentTimeMillis();
            systemBootAfterInit.init(command);
            log.info("{} system boot after cost time: {}ms", systemBootAfterInit.getClass().getName(), System.currentTimeMillis() - start);
        }
    }

}
