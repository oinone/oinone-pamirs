package pro.shushi.pamirs.boot.common.spi.service.infrastructure;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.spi.api.infrastructure.TableBuilderApi;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.util.Map;
import java.util.Set;

/**
 * 启动构建表结构实现
 * <p>
 * 2020/8/27 5:05 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Order(99)
@Component
@SPI.Service
public class DefaultTableBuilder implements TableBuilderApi {

    @Override
    public void build(AppLifecycleCommand command, Map<String/*module*/, Meta> metaMap, Set<String> bootModules) {

    }

}
