package pro.shushi.pamirs.boot.common.spi.api.infrastructure;

import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

import java.util.Map;
import java.util.Set;

/**
 * 启动构建表结构接口
 * <p>
 * 2020/8/27 5:03 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface TableBuilderApi {

    default void buildSys(boolean diffTable) {

    }

    @SuppressWarnings("unused")
    default void build(AppLifecycleCommand command, Map<String/*module*/, Meta> metaMap, Set<String> bootModules) {
    }

}
