package pro.shushi.pamirs.boot.common.spi.service.meta;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.spi.api.meta.MetaDataSaverApi;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.util.Map;
import java.util.Set;

/**
 * 元数据加载API
 * <p>
 * 2020/8/27 5:53 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Order
@Component
@SPI.Service
public class DefaultMetaDataSaver implements MetaDataSaverApi {

    @Override
    public void save(AppLifecycleCommand command, Map<String/*module*/, Meta> metaMap, Set<String> excludeModules) {

    }

}
