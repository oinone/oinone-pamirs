package pro.shushi.pamirs.boot.common.spi.api.meta;

import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.meta.api.dto.meta.MetaData;
import pro.shushi.pamirs.meta.base.common.MetaBaseModel;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * 元数据重新加载API
 * <p>
 * 2020/8/27 5:53 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface MetaDataReLoaderApi {

    Map<String/*module*/, MetaData> load(AppLifecycleCommand command, Set<String> loadModules, Consumer<MetaBaseModel> directive);

    void crossingLoadMetaData(Map<String, MetaData> metaDataMap);

}
