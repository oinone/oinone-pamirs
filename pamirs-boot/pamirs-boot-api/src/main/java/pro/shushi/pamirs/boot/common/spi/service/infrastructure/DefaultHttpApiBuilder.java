package pro.shushi.pamirs.boot.common.spi.service.infrastructure;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.spi.api.infrastructure.HttpApiBuilderApi;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.util.List;
import java.util.Set;

/**
 * 启动构建http协议接口
 * <p>
 * 2020/8/27 5:05 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Order
@Component
@SPI.Service
public class DefaultHttpApiBuilder implements HttpApiBuilderApi {

    @Override
    public void build(AppLifecycleCommand command, Set<String> runModuleSet, List<Meta> metaList) {
        // do nothing.
    }
}
